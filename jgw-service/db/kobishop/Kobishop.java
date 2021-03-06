/*
 * This file is generated by jOOQ.
*/
package kobishop;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import kobishop.tables.Account;
import kobishop.tables.Brand;
import kobishop.tables.Order;
import kobishop.tables.Product;
import kobishop.tables.Type;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Kobishop extends SchemaImpl {

    private static final long serialVersionUID = 536164744;

    /**
     * The reference instance of <code>kobishop</code>
     */
    public static final Kobishop KOBISHOP = new Kobishop();

    /**
     * The table <code>kobishop.account</code>.
     */
    public final Account ACCOUNT = kobishop.tables.Account.ACCOUNT;

    /**
     * The table <code>kobishop.brand</code>.
     */
    public final Brand BRAND = kobishop.tables.Brand.BRAND;

    /**
     * The table <code>kobishop.order</code>.
     */
    public final Order ORDER = kobishop.tables.Order.ORDER;

    /**
     * The table <code>kobishop.product</code>.
     */
    public final Product PRODUCT = kobishop.tables.Product.PRODUCT;

    /**
     * The table <code>kobishop.type</code>.
     */
    public final Type TYPE = kobishop.tables.Type.TYPE;

    /**
     * No further instances allowed
     */
    private Kobishop() {
        super("kobishop", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Account.ACCOUNT,
            Brand.BRAND,
            Order.ORDER,
            Product.PRODUCT,
            Type.TYPE);
    }
}
