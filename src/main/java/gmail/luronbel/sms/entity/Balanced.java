package gmail.luronbel.sms.entity;

/**
 * The class that implements this interface states that it can be either balanced or unbalanced.
 */
public interface Balanced {

    boolean isBalanced();

    boolean isNotBalanced();

    void setBalanced();
}
