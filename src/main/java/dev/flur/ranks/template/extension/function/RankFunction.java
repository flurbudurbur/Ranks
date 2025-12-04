package dev.flur.ranks.template.extension.function;

import dev.flur.ranks.service.RanksService;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pebble function for getting rank data.
 * <p>
 * Usage in templates:
 * <ul>
 *   <li>{@code {{ rank('knight') }}} - Get rank by ID</li>
 *   <li>{@code {{ rank('knight').id }}} - Get rank ID</li>
 *   <li>{@code {{ rank('knight').position }}} - Get position in rank list</li>
 *   <li>{@code {{ rank('knight').isFirst }}} - Check if first rank</li>
 *   <li>{@code {{ rank('knight').isLast }}} - Check if last rank</li>
 *   <li>{@code {{ rank('knight').steps }}} - Get outgoing step targets</li>
 * </ul>
 * <p>
 * Returns a map with rank properties for easy access in templates.
 */
public class RankFunction implements Function {

    private final RanksService ranksService;

    public RankFunction(@NotNull RanksService ranksService) {
        this.ranksService = ranksService;
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self,
                          EvaluationContext context, int lineNumber) {
        Object nameArg = args.get("name");
        if (nameArg == null) {
            return null;
        }

        String rankId = nameArg.toString();
        List<String> allRanks = ranksService.getAllRanks();

        if (!allRanks.contains(rankId)) {
            return null;
        }

        return createRankMap(rankId, allRanks);
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of("name");
    }

    /**
     * Creates a map of rank properties for template access.
     */
    private Map<String, Object> createRankMap(String rankId, List<String> allRanks) {
        Map<String, Object> map = new HashMap<>();

        int position = allRanks.indexOf(rankId) + 1; // 1-based position

        // Basic info
        map.put("id", rankId);
        map.put("name", rankId); // Same as ID for now
        map.put("position", position);
        map.put("total", allRanks.size());

        // Position flags
        map.put("isFirst", position == 1);
        map.put("isLast", position == allRanks.size());

        // Root/Terminal checks
        boolean isRootRank = isRootRank(rankId, allRanks);
        boolean isTerminalRank = ranksService.getNextRanks(rankId).isEmpty();
        map.put("isRootRank", isRootRank);
        map.put("isTerminalRank", isTerminalRank);

        // Outgoing steps (next rank IDs)
        Map<String, String> nextRanks = ranksService.getNextRanks(rankId);
        map.put("steps", List.copyOf(nextRanks.keySet()));

        return map;
    }

    private boolean isRootRank(String rank, List<String> allRanks) {
        // A root rank has no incoming transitions
        for (String otherRank : allRanks) {
            if (!otherRank.equals(rank)) {
                Map<String, String> nextRanks = ranksService.getNextRanks(otherRank);
                if (nextRanks.containsKey(rank)) {
                    return false;
                }
            }
        }
        return true;
    }
}
