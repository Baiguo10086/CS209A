import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class test {

    public static Map<String, Integer> get() {

        Map<String, Integer> result = new HashMap<>();
        String institution = "aabb";
        int participants = 111;
        result.put(institution, participants);
        if (result.containsKey(institution)) {
            participants += result.get(institution);
        }
        result.put(institution, participants);
        Map<String, Integer> sortedResult = new TreeMap<>(result);
        return sortedResult;
    }

    public static void main(String[] args) {
        Map<String, Integer> init = get();
        System.out.println(init.size());
        System.out.println(init);
        
        
    }

}
