package example;

import kr.itanoss.Interesting;
import lombok.Value;

@Interesting
@Value
public class SomeValue {
    private final int id;
    private final String name;
}
