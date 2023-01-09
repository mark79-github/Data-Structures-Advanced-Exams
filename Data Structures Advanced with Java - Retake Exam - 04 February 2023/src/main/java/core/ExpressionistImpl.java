package core;

import models.Expression;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionistImpl implements Expressionist {

    private final Map<String, Expression> expressions;
    private final Map<String, Expression> parents;

    public ExpressionistImpl() {
        this.expressions = new LinkedHashMap<>();
        this.parents = new LinkedHashMap<>();
    }

    @Override
    public void addExpression(Expression expression) {
        if (!this.expressions.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.expressions.put(expression.getId(), expression);
        this.parents.put(expression.getId(), null);
    }

    @Override
    public void addExpression(Expression expression, String parentId) {
        if (!this.expressions.containsKey(parentId)) {
            throw new IllegalArgumentException();
        }
        Expression parent = this.expressions.get(parentId);
        Expression leftChild = parent.getLeftChild();
        Expression rightChild = parent.getRightChild();
        if (leftChild != null && rightChild != null) {
            throw new IllegalArgumentException();
        }
        if (leftChild == null) {
            parent.setLeftChild(expression);
        } else {
            parent.setRightChild(expression);
        }
        this.expressions.put(expression.getId(), expression);
        this.parents.put(expression.getId(), parent);
    }

    @Override
    public boolean contains(Expression expression) {
        return this.expressions.containsKey(expression.getId());
    }

    @Override
    public int size() {
        return this.expressions.size();
    }

    @Override
    public Expression getExpression(String expressionId) {
        if (!this.expressions.containsKey(expressionId)) {
            throw new IllegalArgumentException();
        }
        return this.expressions.get(expressionId);
    }

    @Override
    public void removeExpression(String expressionId) {
        if (!this.expressions.containsKey(expressionId)) {
            throw new IllegalArgumentException();
        }
        Expression expression = this.expressions.get(expressionId);

        Expression parent = this.parents.get(expressionId);
        if (parent != null) {
            parent.setLeftChild(parent.getRightChild());
            parent.setRightChild(null);
        }

        Deque<Expression> deque = new ArrayDeque<>();
        deque.offer(expression);
        while (!deque.isEmpty()) {
            Expression currentExpression = deque.poll();
            Expression leftChild = currentExpression.getLeftChild();
            Expression rightChild = currentExpression.getRightChild();
            if (leftChild != null) {
                deque.offer(leftChild);
            }
            if (rightChild != null) {
                deque.offer(rightChild);
            }
            this.expressions.remove(currentExpression.getId());
            this.parents.remove(currentExpression.getId());
        }
    }

    @Override
    public String evaluate() {
        return postOrder();
    }

    private String postOrder() {
        Expression root = this.expressions.values()
                .stream()
                .findFirst()
                .orElse(null);
        return postOrder(root);
    }

    private String postOrder(Expression expression) {
        if (expression == null) {
            return "";
        }
        postOrder(expression.getLeftChild());
        postOrder(expression.getRightChild());
        return expression.toString();
    }
}
