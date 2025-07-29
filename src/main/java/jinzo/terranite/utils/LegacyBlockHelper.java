package jinzo.terranite.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jinzo.terranite.utils.LegacyBlockHelper.LegacyBlock;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class LegacyBlockHelper {
    private static List<LegacyBlock> legacyBlocks;

    public static class LegacyBlock {
        public int id;
        public String name;
    }

    public static void loadLegacyBlocks() {
        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(LegacyBlockHelper.class.getResourceAsStream("/legacy_blocks.json")))) {
            Type listType = new TypeToken<List<LegacyBlock>>() {}.getType();
            legacyBlocks = new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LegacyBlock findById(int id) {
        try {
            if (legacyBlocks == null) return null;
            return legacyBlocks.stream()
                    .filter(block -> block.id == id)
                    .findFirst().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
