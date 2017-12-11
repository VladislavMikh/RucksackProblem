import java.util.Arrays;
//import org.junit.Test;

public class TheSchrodingerTests {
    //@Test
    public void first() {
        Rucksack myRucksack = new Rucksack(15, new int[]{2, 5, 23, 6}, new int[]{3, 9, 12, 6}, 10000);
        System.out.println(Arrays.toString(myRucksack.findBest()));
    }
}
// вряд ли тесты окажутся тут раньше, чем вы прочитаете это сообщение.
