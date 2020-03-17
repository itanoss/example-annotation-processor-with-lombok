package example;

import kr.itanoss.Interesting;
import lombok.Data;

@Interesting
@Data
public class SomeData {
    private final int id;
    private final String name;
}
