package clienteOdysseyV2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class LyricsGuessing {
	public static List<String> population = new ArrayList<>();

    public static void evolve(String word, int acceptance, int tolerance){
        if (population.size() == 1) {
            System.out.println(population.get(0));
            return;
        }

        List<String> population2 = new ArrayList<>();

        for(String currentWord : population){
            if (Levenshtein(word,currentWord) <= tolerance){
                if(acceptance == -1){
                    //population.remove(currentWord);
                }
                else if(acceptance == 1){
                    population2.add(currentWord);
                }else{
                    break;
                    //randomword
                }
            }else{
                if(acceptance == -1){
                    population2.add(currentWord);
                }
                else if(acceptance == 1){
                    //population.remove(currentWord);
                }else{
                    //randomword
                }
            }
        }
        population = population2;
        for(String currentWord : population){
            System.out.println(currentWord);
        }
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

    public static int Levenshtein(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }
}
