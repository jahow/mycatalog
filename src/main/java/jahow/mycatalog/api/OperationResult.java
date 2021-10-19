package jahow.mycatalog.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OperationResult<K> {
    String errorCode = null;
    K resultValue;

    public boolean hasSucceeded() {
        return errorCode == null;
    }

    static <L> OperationResult<L> ok(L value) {
        return new OperationResult<L>(null, value);
    }

    static OperationResult ok() {
        return new OperationResult(null, null);
    }

    static OperationResult error(String errorCode) {
        return new OperationResult(errorCode, null);
    }
}
