package messages;

/**
 * Created by imran on 1/7/17.
 */
public class Number {
    private Integer number;
    private Boolean prime;

    public Number(Integer number, Boolean prime) {
        this.number = number;
        this.prime = prime;
    }

    public Boolean getPrime() {
        return prime;
    }

    public Integer getNumber() {
        return number;
    }

}
