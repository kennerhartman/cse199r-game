package org.example.tests;

import org.example.serializer.Codec;
import org.example.serializer.JsonOps;
import org.example.serializer.RecordCodecBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;

class CodecTests {
    enum CardType {
        ABILITY, LOOT, ACTION
    }

    record Card(String name, CardType type, float power, double castChance, int bonus, boolean destroyOnPlay) {
        public static final Codec<Card> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("name").forGetter(Card::name),
                        Codec.ofEnum(CardType.class).optionalFieldOf("type", CardType.ABILITY).forGetter(Card::type),
                        Codec.FLOAT.fieldOf("power").forGetter(Card::power),
                        Codec.DOUBLE.fieldOf("castChance").forGetter(Card::castChance),
                        Codec.INT.fieldOf("bonus").forGetter(Card::bonus),
                        Codec.BOOL.fieldOf("destroyOnPlay").forGetter(Card::destroyOnPlay)
                ).apply(Card::new)
        );

        public static final Codec<Card> ROOT_CODEC = CODEC.fieldOf("card");
    }

    @Test
    void testPrimitiveCodecs() {
        assertEquals("hello", Codec.STRING.decode("hello"));
        assertEquals(42, Codec.INT.decode(42));
        assertTrue(Codec.BOOL.decode(true));

        assertEquals("hello", Codec.STRING.encode("hello"));
        assertEquals(42, Codec.INT.encode(42));
        assertTrue((Boolean) Codec.BOOL.encode(true));
    }

    @Test
    void testEnumCodec() {
        Codec<CardType> enumCodec = Codec.ofEnum(CardType.class);

        assertEquals(CardType.ABILITY, enumCodec.decode("ability"));
        assertEquals(CardType.LOOT, enumCodec.decode("loot"));
        assertEquals(CardType.ACTION, enumCodec.decode("action"));

        assertThrows(IllegalStateException.class, () -> enumCodec.decode(123));
    }

    @Test
    void testRecordDecodingSuccess() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Fireball");
        map.put("type", "action");
        map.put("power", 2.0f);
        map.put("castChance", 0.87372);
        map.put("bonus", 10);
        map.put("destroyOnPlay", true);

        Card card = Card.CODEC.decode(map);

        assertEquals("Fireball", card.name());
        assertEquals(CardType.ACTION, card.type());
        assertEquals(2.0f, card.power());
        assertEquals(0.87372, card.castChance());
        assertEquals(10, card.bonus());
        assertTrue(card.destroyOnPlay());
    }

    @Test
    void testOptionalFieldFallback() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Health Potion");
        map.put("bonus", 5);
        map.put("power", 2.0f);
        map.put("castChance", 0.87372);
        map.put("destroyOnPlay", false);

        Card card = Card.CODEC.decode(map);

        assertEquals("Health Potion", card.name());
        assertEquals(CardType.ABILITY, card.type());
        assertEquals(2.0f, card.power());
        assertEquals(0.87372, card.castChance());
        assertEquals(5, card.bonus());
        assertFalse(card.destroyOnPlay());
    }

    @Test
    void testMandatoryFieldMissingThrowsException() {
        Map<String, Object> map = new HashMap<>();
        map.put("bonus", 5);
        map.put("destroyOnPlay", false);

        assertThrows(NullPointerException.class, () -> {
            Card.CODEC.decode(map);
        });
    }

    @Test
    void testRootFieldOfWrapperDecoding() {
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("name", "Epic Sword");
        innerMap.put("type", "loot");
        innerMap.put("power", 2.0f);
        innerMap.put("castChance", 0.87372);
        innerMap.put("bonus", 25);
        innerMap.put("destroyOnPlay", false);

        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("card", innerMap);

        Card card = Card.ROOT_CODEC.decode(rootMap);

        assertEquals("Epic Sword", card.name());
        assertEquals(CardType.LOOT, card.type());
        assertEquals(2.0f, card.power());
        assertEquals(0.87372, card.castChance());
        assertEquals(25, card.bonus());
    }

    @Test
    void testJsonOpsEncoding() {
        Card card = new Card("Shield", CardType.ABILITY, 2.0f, 0.87372, 15, true);

        Object encodedRaw = Card.ROOT_CODEC.encode(card);
        String encoded = Card.ROOT_CODEC.encode(JsonOps.INSTANCE, card);

        assertNotNull(encodedRaw);
        assertNotNull(encoded);
    }

    @Test
    void testJsonOpsDecoding() {
        String jsonString = "{\"card\": {\"name\": \"Shield\", \"type\": \"ABILITY\", \"power\": 2.0, \"castChance\": 0.87372, \"bonus\": 15, \"destroyOnPlay\": true}}";

        Gson gson = new Gson();
        Map<?, ?> jsonMap = gson.fromJson(jsonString, Map.class);

        Card decodedCard = Card.ROOT_CODEC.decode(jsonMap);

        assertEquals("Shield", decodedCard.name());
        assertEquals(CardType.ABILITY, decodedCard.type());
        assertEquals(2.0f, decodedCard.power());
        assertEquals(0.87372, decodedCard.castChance());
        assertEquals(15, decodedCard.bonus());
        assertTrue(decodedCard.destroyOnPlay());
    }

    @Test
    void testCodecLists() {
        record CodecList(List<String> strings) {
            static final Codec<CodecList> CODEC = RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.STRING.listOf().fieldOf("strings").forGetter(CodecList::strings)
                    ).apply(CodecList::new)
            );
        }

        List<String> strings = List.of("a", "b", "c", "d", "e", "f", "g", "h");

        CodecList test = new CodecList(strings);
        String encodedTest = CodecList.CODEC.encode(test).toString();

        assertEquals("{strings=[a, b, c, d, e, f, g, h]}", encodedTest);

        String jsonString = "{\"strings\": [\"a\", \"b\", \"c\", \"d\", \"e\", \"f\", \"g\", \"h\"]}";

        Gson gson = new Gson();
        Map<?, ?> jsonMap = gson.fromJson(jsonString, Map.class);

        CodecList decodedTest = CodecList.CODEC.decode(jsonMap);

        assertEquals(test.strings(), decodedTest.strings());
    }
}