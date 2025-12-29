package xyz.ytora.sql4j.orm.querygen.support;//package xyz.ytora.base.querygen.support;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import xyz.ytora.base.querygen.AbsWhere;
//import xyz.ytora.base.querygen.token.Token;
//import xyz.ytora.sql4j.sql.select.SelectBuilder;
//import xyz.ytora.ytool.str.Strs;
//
//import java.util.List;
//
///**
// * or查询，例如age_or = 1,2,3，可以变成age=1 or age=2 or age=3
// * <pre/>
// * 可以和like查询配合使用，比如age_or=*1*,*2*,*3* -> age like '%1%' or age like '%2%' or age like '%3%'
// */
//@Component
//public class OrWhere extends AbsWhere {
//
//    @Autowired
//    private List<AbsWhere> conditions;
//
//    @Override
//    public Boolean isMatch(Token token) {
//        return Strs.isNotEmpty(token.getKey()) && token.getKey().endsWith("_or");
//    }
//
//    @Override
//    public SelectBuilder apply(SelectBuilder selectBuilder, Token token) {
//        return positiveOrNegate(where, token);
//    }
//
//    @Override
//    protected SelectBuilder positive(SelectBuilder selectBuilder, Token token) {
//        String realKey = token.getKey().substring(0, token.getKey().length() - 3);
//        String[] subValues = token.getValue().split(",");
//        return where.or(nested -> {
//            for (String sub : subValues) {
//                Token t = new Token(realKey, true, sub);
//                for (AbsWhere c : conditions) {
//                    if (Boolean.TRUE.equals(c.isMatch(t))) {
//                        c.apply((SelectBuilder) nested, t);
//                        break;
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    protected SelectBuilder negate(SelectBuilder where, Token token) {
//        // NOT (x1 OR x2 ...) = (NOT x1) AND (NOT x2) ...
//        String realKey = token.getKey().substring(0, token.getKey().length() - 3);
//        String[] subValues = token.getValue().split(",");
//        return where.and(nested -> {
//            for (String sub : subValues) {
//                Token t = new Token(realKey, false, sub); // 直接置为负向
//                for (AbsWhere c : conditions) {
//                    if (Boolean.TRUE.equals(c.isMatch(t))) {
//                        c.apply((SelectBuilder) nested, t);
//                        break;
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getOrder() {
//        return Integer.MAX_VALUE;
//    }
//}
