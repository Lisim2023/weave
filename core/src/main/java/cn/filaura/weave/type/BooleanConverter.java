package cn.filaura.weave.type;

public class BooleanConverter {

    public static Boolean convert(String source) {
        if (source == null) return null;

        String trimmed = source.trim().toUpperCase();
        if (trimmed.length() == 1) {
            char c = trimmed.charAt(0);
            if (c == 'Y' || c == 'T' || c == '1') {
                return Boolean.TRUE;
            } else if (c == 'N' || c == 'F' || c == '0') {
                return Boolean.FALSE;
            }
        }

        if ("TRUE".equals(trimmed)) return Boolean.TRUE;
        if ("FALSE".equals(trimmed)) return Boolean.FALSE;

        return null;
    }
}
