package xyz.ytora.sql4j.orm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 分页相关工具类
 */
public class Pages {

    /**
     * 有时候，分页查询时，分页泛型是数据库实体类型，但是查询出数据后进行返回时，需要将分页的泛型转为vo类型，
     * 就需要进行转换操作，该方法封装了转换操作的代码
     *
     * @param oldPage 原始分页对象
     * @param func 旧分页数据的转换逻辑
     * @param <T>     新分页对象的泛型
     * @return 转换后的新分页对象
     */
    public static <R, T> Page<T> transPage(Page<R> oldPage, Function<R, T> func) {
        return transPage(oldPage, oldPage.getRecords().stream().map(func).toList());
    }


    /**
     * 有时候，分页查询时，分页泛型是数据库实体类型，但是查询出数据后进行返回时，需要将分页的泛型转为vo类型，
     * 就需要进行转换操作，该方法封装了转换操作的代码
     *
     * @param oldPage 原始分页对象
     * @param newList 新分页对象的数据集合
     * @param <T>     新分页对象的泛型
     * @return 转换后的新分页对象
     */
    public static <T> Page<T> transPage(Page<?> oldPage, List<T> newList) {
        Page<T> page = new Page<>();
        page.setPageNo(oldPage.getPageNo());
        page.setPageSize(oldPage.getPageSize());
        page.setTotal(oldPage.getTotal());
        page.setPages(oldPage.getPages());
        page.setRecords(newList);
        return page;
    }

    /**
     * 对list进行分页操作
     *
     * @param pageNo 当前页
     * @param pageSize 每页尺寸
     * @param list 原始集合
     * @return 分页后的数据
     * @param <T> 数据类型
     */
    public static <T> Page<T> toPage(Integer pageNo, Integer pageSize, List<T> list) {
        return toPage(new Page<>(pageNo, pageSize), list);
    }

    /**
     * 对list进行分页操作
     * @param page 分页对象
     * @param list 原始集合
     * @return 分页后的数据
     * @param <T> 数据类型
     */
    public static <T> Page<T> toPage(Page<T> page, List<T> list) {
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();

        //计算偏移量
        int startIndex = (pageNo - 1) * pageSize;
        if (list.size() <= pageSize) {
            page.setPages(1).setPageSize(pageSize)
                    .setPageNo(1).setTotal((long) list.size()).setRecords(list);
        }
        //计算分页数据
        int pages;
        if (list.size() % pageSize == 0) {
            pages = list.size() / pageSize;
        } else {
            pages = list.size() / pageSize + 1;
        }
        if (pageNo * pageSize > list.size()) {
            pageNo = pages;
        }

        List<T> records = new ArrayList<>();
        for (long i = startIndex; i < startIndex + pageSize && i < list.size(); i++) {
            records.add(list.get((int) i));
        }
        //组装分页数据
        page.setPages(pages)
                .setPageSize(pageSize)
                .setPageNo(pageNo)
                .setTotal((long) list.size())
                .setRecords(records);
        return page;
    }

    /**
     * 对于分页查询接口，pageNo和pageSize这两个参数是必须的，每次都定义这两个参数并传递封装这两个成一个Page对象略显繁琐
     * 该方法可以直接从请求对象中获取这两个参数并封装成一个Page对象返回，可以在程序的任何地方调用，故而省去了参数传递
     *
     * @return 分页对象
     */
//    public static <T> Page<T> getPage() {
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = requestAttributes.getRequest();
//        //得到当前页
//        String current = request.getParameter("current");
//        if (Strs.isEmpty(current)) {
//            current = request.getParameter("pageNo");
//        }
//        //得到每页尺寸
//        String size = request.getParameter("size");
//        if (Strs.isEmpty(size)) {
//            size = request.getParameter("pageSize");
//        }
//        int pageNo = Converts.convert(current, Integer.class, 1);
//        int pageSize = Converts.convert(size, Integer.class, 10);
//        return new Page<>(pageNo, pageSize);
//    }

}