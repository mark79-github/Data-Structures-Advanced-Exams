package core;

import models.Expression;
import models.ExpressionType;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class ExpressionistImplTest {

    private final ExpressionistImpl expressionist;

    public ExpressionistImplTest() {
        this.expressionist = new ExpressionistImpl();
    }

    @Test
    public void test_addExpression_whenEmpty() {
        Expression expression = new Expression("1", "1", ExpressionType.VALUE, null, null);
        this.expressionist.addExpression(expression);

        Assert.assertEquals(1, this.expressionist.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removeExpression_shouldThrowException_whenExpressionDoesNotExists() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "2", ExpressionType.VALUE, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);
        Expression expression_4 = new Expression("4", "4", ExpressionType.VALUE, null, null);
        Expression expression_5 = new Expression("5", "5", ExpressionType.VALUE, null, null);
        Expression expression_6 = new Expression("6", "6", ExpressionType.VALUE, null, null);
        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());
        this.expressionist.addExpression(expression_4, expression_2.getId());
        this.expressionist.addExpression(expression_5, expression_2.getId());

        this.expressionist.removeExpression(expression_6.getId());
    }

    @Test
    public void test_removeExpression_shouldRemoveSuccessfully_whenExpressionHasLeftAndRightChild() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "2", ExpressionType.VALUE, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);
        Expression expression_4 = new Expression("4", "4", ExpressionType.VALUE, null, null);
        Expression expression_5 = new Expression("5", "5", ExpressionType.VALUE, null, null);
        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());
        this.expressionist.addExpression(expression_4, expression_2.getId());
        this.expressionist.addExpression(expression_5, expression_2.getId());

        this.expressionist.removeExpression(expression_2.getId());
        Assert.assertEquals(2, this.expressionist.size());
        Assert.assertEquals(expression_1.getLeftChild(), expression_3);
        Assert.assertNull(expression_1.getRightChild());
    }

    @Test
    public void test_removeExpression_shouldRemoveSuccessfully_whenRemoveRoot() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "2", ExpressionType.VALUE, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);
        Expression expression_4 = new Expression("4", "4", ExpressionType.VALUE, null, null);
        Expression expression_5 = new Expression("5", "5", ExpressionType.VALUE, null, null);
        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());
        this.expressionist.addExpression(expression_4, expression_2.getId());
        this.expressionist.addExpression(expression_5, expression_2.getId());

        this.expressionist.removeExpression(expression_1.getId());
        Assert.assertEquals(0, this.expressionist.size());
    }


    @Test
    public void test_removeExpression_shouldRemoveSuccessfully_whenExpressionHasOnlyRoot() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        this.expressionist.addExpression(expression_1);
        this.expressionist.removeExpression(expression_1.getId());
        Assert.assertEquals(0, this.expressionist.size());
    }

    @Test
    public void test_removeExpression_shouldRemoveSuccessfully_whenExpressionHasOnlyLeftChild() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "2", ExpressionType.VALUE, null, null);
        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());

        this.expressionist.removeExpression(expression_2.getId());
        Assert.assertEquals(1, this.expressionist.size());
        Assert.assertNull(expression_1.getLeftChild());
        Assert.assertNull(expression_1.getRightChild());
    }

    @Test
    public void test_evaluate_shouldReturnEmptyString_whenRepoIsEmpty() {
        String actual = this.expressionist.evaluate();
        String expected = "";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_evaluate_shouldReturnEmptyString_whenRepoBecomeEmpty() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "+", ExpressionType.OPERATOR, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);
        Expression expression_4 = new Expression("4", "-", ExpressionType.OPERATOR, null, null);
        Expression expression_5 = new Expression("5", "5", ExpressionType.VALUE, null, null);
        Expression expression_6 = new Expression("6", "6", ExpressionType.VALUE, null, null);
        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());
        this.expressionist.addExpression(expression_4, expression_2.getId());
        this.expressionist.addExpression(expression_5, expression_2.getId());
        this.expressionist.addExpression(expression_6, expression_4.getId());

        this.expressionist.removeExpression(expression_1.getId());
        String actual = this.expressionist.evaluate();
        String expected = "";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_evaluate_shouldReturnCorrectly_whenHasOnlyRootWithExpressionTypeValue() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        this.expressionist.addExpression(expression_1);

        String actual = this.expressionist.evaluate();

        String expected = "1";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_evaluate_shouldReturnCorrectly_whenRepoHasRootWithExpressionTypeOperatorAndChildrenWithTypeValue() {
        Expression expression_1 = new Expression("1", "+", ExpressionType.OPERATOR, null, null);
        Expression expression_2 = new Expression("2", "2", ExpressionType.VALUE, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);

        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());
        String actual = this.expressionist.evaluate();

        String expected = "(2 + 3)";
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getExpression_shouldThrowException_whenRepositoryIsEmpty() {
        this.expressionist.getExpression(UUID.randomUUID().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getExpression_shouldThrowException_whenExpressionIdDoesNotExists() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "+", ExpressionType.OPERATOR, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);
        Expression expression_4 = new Expression("4", "-", ExpressionType.OPERATOR, null, null);
        Expression expression_5 = new Expression("5", "5", ExpressionType.VALUE, null, null);
        Expression expression_6 = new Expression("6", "6", ExpressionType.VALUE, null, null);

        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());
        this.expressionist.addExpression(expression_4, expression_2.getId());
        this.expressionist.addExpression(expression_5, expression_2.getId());

        this.expressionist.getExpression(expression_6.getId());
    }

    @Test
    public void test_getExpression_shouldReturnCorrectly() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "+", ExpressionType.OPERATOR, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);

        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());

        Expression expression = this.expressionist.getExpression(expression_2.getId());

        Assert.assertEquals(expression, expression_2);
    }


    @Test(expected = IllegalArgumentException.class)
    public void test_addExpression_shouldThrowException_whenRepoIsNotEmpty() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "+", ExpressionType.OPERATOR, null, null);

        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2);
    }

    @Test
    public void test_addExpression_shouldAddExpressionCorrectly() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);

        this.expressionist.addExpression(expression_1);

        Assert.assertEquals(1, this.expressionist.size());
        Assert.assertTrue(this.expressionist.contains(expression_1));
        Expression expression = this.expressionist.getExpression("1");
        Assert.assertEquals(expression, expression_1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_addExpressionOverloaded_shouldThrowException_whenExpressionWithParentIdDoesNotExists() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "+", ExpressionType.OPERATOR, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);
        Expression expression_4 = new Expression("4", "-", ExpressionType.OPERATOR, null, null);
        Expression expression_5 = new Expression("5", "5", ExpressionType.VALUE, null, null);
        Expression expression_6 = new Expression("6", "6", ExpressionType.VALUE, null, null);

        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());
        this.expressionist.addExpression(expression_4, expression_2.getId());
        this.expressionist.addExpression(expression_5, expression_6.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_addExpressionOverloaded_shouldThrowException_whenChildrenPresent() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "+", ExpressionType.OPERATOR, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);
        Expression expression_4 = new Expression("4", "-", ExpressionType.OPERATOR, null, null);
        Expression expression_5 = new Expression("5", "5", ExpressionType.VALUE, null, null);
        Expression expression_6 = new Expression("6", "6", ExpressionType.VALUE, null, null);

        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());
        this.expressionist.addExpression(expression_4, expression_2.getId());
        this.expressionist.addExpression(expression_5, expression_2.getId());
        this.expressionist.addExpression(expression_6, expression_2.getId());
    }

    @Test
    public void test_addExpressionOverloaded_shouldSetLeftChildCorrectly() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "+", ExpressionType.OPERATOR, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);
        Expression expression_4 = new Expression("4", "-", ExpressionType.OPERATOR, null, null);

        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());

        Assert.assertNull(expression_2.getLeftChild());
        this.expressionist.addExpression(expression_4, expression_2.getId());
        Assert.assertEquals(expression_2.getLeftChild(), expression_4);
    }

    @Test
    public void test_addExpressionOverloaded_shouldSetRightChildCorrectly() {
        Expression expression_1 = new Expression("1", "1", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "+", ExpressionType.OPERATOR, null, null);
        Expression expression_3 = new Expression("3", "3", ExpressionType.VALUE, null, null);
        Expression expression_4 = new Expression("4", "-", ExpressionType.OPERATOR, null, null);
        Expression expression_5 = new Expression("5", "5", ExpressionType.VALUE, null, null);

        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());
        this.expressionist.addExpression(expression_4, expression_2.getId());

        Assert.assertNull(expression_2.getRightChild());
        this.expressionist.addExpression(expression_5, expression_2.getId());
        Assert.assertEquals(expression_2.getRightChild(), expression_5);
        Assert.assertEquals(5, this.expressionist.size());
    }

    @Test
    public void test_removeExpression_test5() {
        Expression expression_1 = new Expression("1", "+", ExpressionType.VALUE, null, null);
        Expression expression_2 = new Expression("2", "*", ExpressionType.VALUE, null, null);
        Expression expression_3 = new Expression("3", "/", ExpressionType.VALUE, null, null);

        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());

        this.expressionist.removeExpression(expression_3.getId());

        Assert.assertFalse(this.expressionist.contains(expression_3));
        Assert.assertNull(this.expressionist.getExpression(expression_1.getId()).getRightChild());
    }

    @Test
    public void test_evaluate_test10() {
        Expression expression_1 = new Expression("1", "+", ExpressionType.OPERATOR, null, null);
        Expression expression_2 = new Expression("2", "*", ExpressionType.OPERATOR, null, null);
        Expression expression_3 = new Expression("3", "/", ExpressionType.OPERATOR, null, null);
        Expression expression_4 = new Expression("4", "5", ExpressionType.VALUE, null, null);
        Expression expression_5 = new Expression("5", "10", ExpressionType.VALUE, null, null);
        Expression expression_6 = new Expression("6", "2.5", ExpressionType.VALUE, null, null);
        Expression expression_7 = new Expression("7", "3.5", ExpressionType.VALUE, null, null);

        this.expressionist.addExpression(expression_1);
        this.expressionist.addExpression(expression_2, expression_1.getId());
        this.expressionist.addExpression(expression_3, expression_1.getId());
        this.expressionist.addExpression(expression_4, expression_2.getId());
        this.expressionist.addExpression(expression_5, expression_2.getId());
        this.expressionist.addExpression(expression_6, expression_3.getId());
        this.expressionist.addExpression(expression_7, expression_3.getId());

        String evaluate = this.expressionist.evaluate();
        String actual = "((5 * 10) + (2.5 / 3.5))";
        Assert.assertEquals(actual, evaluate);
    }
}