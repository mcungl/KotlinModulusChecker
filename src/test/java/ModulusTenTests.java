import com.dambra.paul.moduluschecker.*;
import com.dambra.paul.moduluschecker.Account.BankAccount;
import com.dambra.paul.moduluschecker.chain.checks.ModulusTenCheck;
import com.dambra.paul.moduluschecker.valacdosFile.WeightRow;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ModulusTenTests {
    @Test
    public void CanRunModulusTenCheck() {
        String sc = "089999";
        String an = "66374958";
        WeightRow row = new WeightRow(
                ModulusAlgorithm.MOD10,
                ImmutableList.of(0, 0, 0, 0, 0, 0, 7, 1, 3, 7, 1, 3, 7, 1),
                Optional.empty()
        );

        ModulusCheckParams params = new ModulusCheckParams(
                BankAccount.Of(sc, an),
                Optional.of(row), Optional.empty(), Optional.empty());
        ModulusTenCheck checker = new ModulusTenCheck();


        Boolean result = checker.check(params, x -> Optional.ofNullable(
                WeightRow.copy(x.firstWeightRow.orElse(null))).get());

        assertThat(result, is(equalTo(true)));
    }
}
