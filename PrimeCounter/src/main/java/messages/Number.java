package messages;

/**
 * Created by imran on 1/7/17.
 */
public class Number {
    private Long number;
    private Boolean prime;

    public Number(Long number, Boolean prime) {
        this.number = number;
        this.prime = prime;
    }

    public Boolean getPrime() {
        return prime;
    }

    public Long getNumber() {
        return number;
    }

}
