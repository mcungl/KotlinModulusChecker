package com.dambra.paul.moduluschecker.chain;

import com.dambra.paul.moduluschecker.ModulusCheckParams;
import com.dambra.paul.moduluschecker.SortCodeSubstitution;
import com.dambra.paul.moduluschecker.chain.checks.DoubleAlternateCheck;
import com.dambra.paul.moduluschecker.chain.checks.ExceptionFiveModulusElevenCheck;
import com.dambra.paul.moduluschecker.chain.checks.ModulusElevenCheck;
import com.dambra.paul.moduluschecker.chain.checks.ModulusTenCheck;
import com.dambra.paul.moduluschecker.valacdosFile.WeightRow;

import java.util.Optional;
import java.util.function.Function;

public final class FirstModulusCheckRouter implements ModulusChainCheck {
    private final SortCodeSubstitution sortCodeSubstitution;
    private final SecondCheckRequiredGate next;

    public FirstModulusCheckRouter(
            SortCodeSubstitution sortCodeSubstitution,
            SecondCheckRequiredGate exceptionTwoGate) {
        this.sortCodeSubstitution = sortCodeSubstitution;
        this.next = exceptionTwoGate;
    }

    @Override
    public ModulusResult check(ModulusCheckParams params) {

        boolean result = false;

        Function<ModulusCheckParams, WeightRow> rowSelector = p -> p.firstWeightRow.get();

        switch (params.firstWeightRow.get().modulusAlgorithm) {
            case DOUBLE_ALTERNATE:
                result = new DoubleAlternateCheck().check(params, rowSelector);
                break;
            case MOD10:
                result = new ModulusTenCheck().check(params, rowSelector);
                break;
            case MOD11:
                result = params.firstWeightRow.get().isException(5)
                        ? new ExceptionFiveModulusElevenCheck(sortCodeSubstitution).check(params, rowSelector)
                        : new ModulusElevenCheck().check(params, rowSelector);
                break;
        }

        final ModulusCheckParams nextCheckParams = addResultToParamsForNextCheck(params, result);

        return next.check(nextCheckParams);
    }

    private ModulusCheckParams addResultToParamsForNextCheck(ModulusCheckParams params, boolean result) {
        ModulusResult modulusResult = new ModulusResult(Optional.of(result), Optional.empty());
        modulusResult = modulusResult.withFirstException(
                params.firstWeightRow.flatMap(weightRow -> weightRow.exception)
        );

        return new ModulusCheckParams(
                params.account,
                params.firstWeightRow,
                params.secondWeightRow,
                Optional.of(modulusResult)
        );
    }

}
