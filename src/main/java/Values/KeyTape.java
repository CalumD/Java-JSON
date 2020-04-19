package Values;

import java.util.ArrayList;
import java.util.List;

public class KeyTape extends Tape<String> {

    public KeyTape(String fullInput) {
        super(fullInput);
    }

    @Override
    public String parseNextElement() {
        return null;
    }

    public List<String> parseAllElements() {
        return new ArrayList<>();
    }
}
