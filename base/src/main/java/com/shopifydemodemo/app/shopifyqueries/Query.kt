package com.shopifydemodemo.app.shopifyqueries

import android.util.Log
import com.shopify.buy3.Storefront
import com.shopify.buy3.Storefront.*
import com.shopify.buy3.Storefront.ProductQuery.*
import com.shopify.buy3.Storefront.ProductVariantQuery.PresentmentPricesArguments
import com.shopify.graphql.support.ID

object Query {
    private val TAG = "Query"
    val shopDetails: QueryRootQuery
        get() = Storefront.query { q ->
            q
                .shop { shop ->
                    shop
                        .paymentSettings { pay ->
                            pay.enabledPresentmentCurrencies().currencyCode()
                        }
                }

        }

    fun productDefinition(list_currency: List<Storefront.CurrencyCode>): Storefront.ProductConnectionQueryDefinition {
        return Storefront.ProductConnectionQueryDefinition { productdata ->
            productdata.edges({ edges ->
                    edges
                        .cursor().node({ node ->
                            node
                                .handle()
                                .title()
                                .images({ img -> img.first(10) }, { imag ->
                                    imag.edges({ imgedge ->
                                        imgedge
                                            .node({ imgnode ->
                                                imgnode
                                                    .originalSrc()
                                                    .transformedSrc({ t ->
                                                        t
                                                            .maxWidth(600)
                                                            .maxHeight(600)
                                                    }
                                                    )
                                            }
                                            )
                                    }
                                    )
                                }
                                )
                                .media({ m -> m.first(10) }, { me ->
                                    me.edges({ e ->
                                        e.node({ n ->
                                            n.onMediaImage { media ->
                                                media.previewImage { p ->
                                                    p.originalSrc()
                                                }
                                            }
                                                .onExternalVideo { _queryBuilder ->
                                                    _queryBuilder.embeddedUrl()
                                                        .previewImage {
                                                            it.originalSrc()
                                                        }
                                                }
                                                .onVideo(VideoQueryDefinition {
                                                    it.previewImage {
                                                        it.originalSrc()
                                                    }.sources { it ->
                                                        it.url()
                                                    }
                                                })
                                                .onModel3d({ md ->
                                                    md
                                                        .sources({ s -> s.url() })
                                                        .previewImage({ p -> p.originalSrc() })
                                                })
                                        })
                                    })
                                })
                                .availableForSale()
                                .descriptionHtml()
                                .description()
                                .tags()
                                .vendor()
                                .handle()
                                .totalInventory()
                                .variants({ args ->
                                    args
                                        .first(120)
                                }, { variant ->
                                    variant
                                        .edges({ variantEdgeQuery ->
                                            variantEdgeQuery
                                                .node({ productVariantQuery ->
                                                    productVariantQuery
                                                        .priceV2({ price ->
                                                            price.amount().currencyCode()
                                                        })
//                                                        .storeAvailability({ args -> args.first(20) },
//                                                            { storeAvail ->
//                                                                storeAvail.edges({ storeAvailEdges ->
//                                                                    storeAvailEdges.node({
//                                                                        it.available()
//                                                                        it.pickUpTime()
//                                                                        it.location {
//                                                                            it.name()
//                                                                            it.address {
//                                                                                it.address1()
//                                                                                it.address2()
//                                                                                it.city()
//                                                                                it.country()
//                                                                                it.province()
//                                                                                it.zip()
//                                                                                it.phone()
//                                                                            }
//                                                                        }
//                                                                    })
//                                                                })
//                                                            })
                                                        .price()
                                                        .title()
                                                        .quantityAvailable()
                                                        .presentmentPrices(
                                                            { arg ->
                                                                arg.first(25).presentmentCurrencies(
                                                                    list_currency
                                                                )
                                                            },
                                                            { price ->
                                                                price.edges({ e ->
                                                                    e.cursor().node({ n ->
                                                                        n.price({ p ->
                                                                            p.amount()
                                                                                .currencyCode()
                                                                        }).compareAtPrice({ cp ->
                                                                            cp.amount()
                                                                                .currencyCode()
                                                                        })
                                                                    })
                                                                })
                                                            })
                                                        .selectedOptions({ select ->
                                                            select.name().value()
                                                        })
                                                        .compareAtPriceV2({ compare ->
                                                            compare.amount().currencyCode()
                                                        })
                                                        .compareAtPrice()
                                                        .currentlyNotInStock()
                                                        .image({ image ->
                                                            image.transformedSrc({ tr ->
                                                                tr.maxHeight(
                                                                    600
                                                                ).maxWidth(600)
                                                            }).originalSrc()
                                                        })
                                                        .availableForSale()
                                                        .sku()
                                                }
                                                )
                                        }
                                        )
                                }
                                )
                                .onlineStoreUrl()
                                .options({ op ->
                                    op.name()
                                        .values()
                                }
                                )
                        })
                }
                )
                .pageInfo(Storefront.PageInfoQueryDefinition { it.hasNextPage() }
                )
        }
    }

    fun recommendedProducts(
        product_id: String,
        list_currency: List<Storefront.CurrencyCode>
    ): QueryRootQuery {
        return Storefront.query { root ->
            root.productRecommendations(ID(product_id), productQuery(list_currency))
        }
    }

    fun productQuery(list_currency: List<Storefront.CurrencyCode>): Storefront.ProductQueryDefinition {
        return Storefront.ProductQueryDefinition { product ->
            product
                .title()
                .images({ img -> img.first(10) }, { imag ->
                    imag.edges({ imgedge ->
                        imgedge.node({ imgnode ->
                            imgnode.originalSrc()
                                .transformedSrc({ tr -> tr.maxWidth(600).maxHeight(600) })
                        }
                        )
                    }
                    )
                }
                )
                .availableForSale()
                .descriptionHtml()
                .description()
                .totalInventory()
                .tags()
                .handle()
                .media({ m -> m.first(10) }, { me ->
                    me.edges({ e ->
                        e.node({ n ->
                            n.onMediaImage { media ->
                                media.previewImage { p ->
                                    p.originalSrc()
                                }
                            }
                                .onExternalVideo { _queryBuilder ->
                                    _queryBuilder.embeddedUrl()
                                        .previewImage {
                                            it.originalSrc()
                                        }
                                }
                                .onVideo(VideoQueryDefinition {
                                    it.previewImage {
                                        it.originalSrc()
                                    }.sources { it ->
                                        it.url()
                                    }
                                })
                                .onModel3d({ md ->
                                    md
                                        .sources({ s -> s.url() })
                                        .previewImage({ p -> p.originalSrc() })
                                })
                        })
                    })
                })
                .vendor()
                .variants({ args ->
                    args
                        .first(120)
                }, { variant ->
                    variant
                        .edges({ variantEdgeQuery ->
                            variantEdgeQuery
                                .node({ productVariantQuery ->
                                    productVariantQuery
                                        .title()
                                  /*      .storeAvailability({ args -> args.first(20) },
                                            { storeAvail ->
                                                storeAvail.edges({ storeAvailEdges ->
                                                    storeAvailEdges.node({
                                                        it.available()
                                                        it.pickUpTime()
                                                        it.location {
                                                            it.name()
                                                            it.address {
                                                                it.address1()
                                                                it.address2()
                                                                it.city()
                                                                it.country()
                                                                it.province()
                                                                it.zip()
                                                                it.phone()
                                                            }
                                                        }
                                                    })
                                                })
                                            })*/
                                        .priceV2({ p -> p.amount().currencyCode() })
                                        .quantityAvailable()
                                        .currentlyNotInStock()
                                        .presentmentPrices(
                                            { a ->
                                                a.first(50).presentmentCurrencies(list_currency)
                                            },
                                            { pre ->
                                                pre.edges({ ed ->
                                                    ed.node({ n ->
                                                        n.price({ p ->
                                                            p.currencyCode().amount()
                                                        }).compareAtPrice({ cp ->
                                                            cp.amount().currencyCode()
                                                        })
                                                    }).cursor()
                                                })
                                            })
                                        .selectedOptions({ select -> select.name().value() })
                                        .compareAtPriceV2({ c -> c.amount().currencyCode() })
                                        .image(Storefront.ImageQueryDefinition {
                                            it.originalSrc().transformedSrc()
                                        })
                                        .availableForSale()
                                        .sku()
                                }
                                )
                        }
                        )
                }
                )
                .onlineStoreUrl()
                .options({ op ->
                    op.name()
                        .values()
                }
                )
        }
    }

    fun getProductsById(
        cat_id: String?,
        cursor: String,
        sortby_key: Storefront.ProductCollectionSortKeys?,
        direction: Boolean,
        number: Int,
        list_currency: List<Storefront.CurrencyCode>
    ): QueryRootQuery {
        val definition: Storefront.CollectionQuery.ProductsArgumentsDefinition
        if (cursor == "nocursor") {
            if (sortby_key != null) {
                definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).sortKey(sortby_key).reverse(direction)
                }
            } else {
                definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).reverse(direction)
                }
            }

        } else {
            if (sortby_key != null) {
                definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).after(cursor).sortKey(sortby_key).reverse(direction)
                }
            } else {
                definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).after(cursor).reverse(direction)
                }
            }
        }
        return Storefront.query { root ->
            root
                .node(
                    ID(cat_id)
                ) { rootnode ->
                    rootnode.onCollection { oncollection ->
                        oncollection
                            .handle()
                            .image { image ->
                                image
                                    .originalSrc()
                                    .transformedSrc { tr ->
                                        tr
                                            .maxHeight(300)
                                            .maxWidth(700)
                                    }
                            }
                            .title()
                            .products(
                                definition, productDefinition(list_currency)
                            )
                    }
                }
        }
    }

    fun getAllProductsByID(
        id: List<ID>,
        list_currency: List<Storefront.CurrencyCode>
    ): QueryRootQuery {
        return Storefront.query { root: QueryRootQuery ->
            root
                .nodes(
                    id
                ) { n: NodeQuery ->
                    n.onProduct { p: ProductQuery ->
                        p.title()
                            .handle()
                            .vendor()
                            .tags()
                            .collections({ getconn: CollectionsArguments -> getconn.first(100) }
                            ) { conn: CollectionConnectionQuery ->
                                conn
                                    .edges { edgeconn: CollectionEdgeQuery ->
                                        edgeconn
                                            .node { nodeconn: CollectionQuery ->
                                                nodeconn
                                                    .title()
                                            }
                                    }
                            }
                            .images({ img: ImagesArguments -> img.first(10) }
                            ) { imag: ImageConnectionQuery ->
                                imag.edges { imgedge: ImageEdgeQuery ->
                                    imgedge
                                        .node { imgnode: ImageQuery ->
                                            imgnode
                                                .originalSrc()
                                                .transformedSrc()
                                        }
                                }
                            }
                            .totalInventory()
                            .availableForSale()
                            .descriptionHtml()
                            .description()
                            .onlineStoreUrl()
                            .options { option -> option.name().values() }
                            .handle()
                            .media({ m -> m.first(10) }, { me ->
                                me.edges({ e ->
                                    e.node({ n ->
                                        n.onMediaImage { media ->
                                            media.previewImage { p ->
                                                p.originalSrc()
                                            }
                                        }
                                            .onExternalVideo { _queryBuilder ->
                                                _queryBuilder.embeddedUrl()
                                                    .previewImage {
                                                        it.originalSrc()
                                                    }
                                            }
                                            .onVideo(VideoQueryDefinition {
                                                it.previewImage {
                                                    it.originalSrc()
                                                }.sources { it ->
                                                    it.url()
                                                }
                                            })
                                            .onModel3d({ md ->
                                                md
                                                    .sources({ s -> s.url() })
                                                    .previewImage({ p -> p.originalSrc() })
                                            })
                                    })
                                })
                            })
                            .variants({ args: VariantsArguments ->
                                args
                                    .first(120)
                            }
                            ) { variant: ProductVariantConnectionQuery ->
                                variant
                                    .edges { variantEdgeQuery: ProductVariantEdgeQuery ->
                                        variantEdgeQuery
                                            .node { productVariantQuery: ProductVariantQuery ->
                                                productVariantQuery
                                                    .priceV2 { price: MoneyV2Query ->
                                                        price.amount().currencyCode()
                                                    }
                                                    .presentmentPrices({ arg: PresentmentPricesArguments ->
                                                        arg.first(
                                                            25
                                                        ).presentmentCurrencies(list_currency)
                                                    }) { price: ProductVariantPricePairConnectionQuery ->
                                                        price.edges { e: ProductVariantPricePairEdgeQuery ->
                                                            e.cursor()
                                                                .node { na: ProductVariantPricePairQuery ->
                                                                    na.price { pr: MoneyV2Query ->
                                                                        pr.amount().currencyCode()
                                                                    }
                                                                        .compareAtPrice { cp: MoneyV2Query ->
                                                                            cp.amount()
                                                                                .currencyCode()
                                                                        }
                                                                }
                                                        }
                                                    }
                                                    .price()
                                                    .quantityAvailable()
                                                    .currentlyNotInStock()
                                                    .title()
//                                                    .storeAvailability({ args -> args.first(20) },
//                                                        { storeAvail ->
//                                                            storeAvail.edges({ storeAvailEdges ->
//                                                                storeAvailEdges.node({
//                                                                    it.available()
//                                                                    it.pickUpTime()
//                                                                    it.location {
//                                                                        it.name()
//                                                                        it.address {
//                                                                            it.address1()
//                                                                            it.address2()
//                                                                            it.city()
//                                                                            it.country()
//                                                                            it.province()
//                                                                            it.zip()
//                                                                            it.phone()
//                                                                        }
//                                                                    }
//                                                                })
//                                                            })
//                                                        })
                                                    .selectedOptions { select: SelectedOptionQuery ->
                                                        select.name().value()
                                                    }
                                                    .compareAtPriceV2 { compare: MoneyV2Query ->
                                                        compare.amount().currencyCode()
                                                    }
                                                    .compareAtPrice()
                                                    .image({ image ->
                                                        image.transformedSrc({ tr ->
                                                            tr.maxHeight(
                                                                600
                                                            ).maxWidth(600)
                                                        }).originalSrc()
                                                    })
                                                    .availableForSale()
                                                    .sku()
                                            }
                                    }
                            }
                    }
                }
        }
    }

    fun getProductsByHandle(
        handle: String,
        cursor: String,
        sortby_key: Storefront.ProductCollectionSortKeys?,
        direction: Boolean,
        number: Int,
        list_currency: List<Storefront.CurrencyCode>
    ): QueryRootQuery {
        val definition: Storefront.CollectionQuery.ProductsArgumentsDefinition
        if (cursor == "nocursor") {
            if (sortby_key != null) {
                definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).sortKey(sortby_key).reverse(direction)
                }
            } else {
                definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).reverse(direction)
                }
            }

        } else {
            if (sortby_key != null) {
                definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).after(cursor).sortKey(sortby_key).reverse(direction)
                }
            } else {
                definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).after(cursor).reverse(direction)
                }
            }
        }
        return Storefront.query { root ->
            root.collectionByHandle(handle) { collect ->
                collect.products(
                    definition,
                    productDefinition(list_currency)
                )
            }
        }
    }

    fun getAllProducts(
        cursor: String,
        sortby_key: Storefront.ProductSortKeys?,
        direction: Boolean,
        number: Int,
        list_currency: List<Storefront.CurrencyCode>
    ): QueryRootQuery {
        val shoppro: QueryRootQuery.ProductsArgumentsDefinition
        if (cursor == "nocursor") {
            if (sortby_key != null) {
                shoppro = QueryRootQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).sortKey(sortby_key).reverse(direction)
                }
            } else {
                shoppro = QueryRootQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).reverse(direction)
                }
            }
        } else {
            if (sortby_key != null) {
                shoppro = QueryRootQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).after(cursor).sortKey(sortby_key).reverse(direction)
                }
            } else {
                shoppro = QueryRootQuery.ProductsArgumentsDefinition { args ->
                    args.first(number).after(cursor).reverse(direction)
                }
            }
        }
        return Storefront.query { root ->
            root.products(
                shoppro,
                productDefinition(list_currency)
            )
        }
    }

    fun getCollections(cursor: String): QueryRootQuery {
        val definition: QueryRootQuery.CollectionsArgumentsDefinition
        if (cursor == "nocursor") {
            definition =
                QueryRootQuery.CollectionsArgumentsDefinition { args -> args.first(250) }
        } else {
            definition = QueryRootQuery.CollectionsArgumentsDefinition { args ->
                args.first(250).after(cursor)
            }
        }
        return Storefront.query { root ->
            root.collections(definition, collectiondef)
        }
    }

    private val collectiondef: Storefront.CollectionConnectionQueryDefinition
        get() = Storefront.CollectionConnectionQueryDefinition { collect ->
            collect
                .edges({ edge ->
                    edge
                        .cursor()
                        .node({ node ->
                            node.title().image({ image -> image.originalSrc().transformedSrc() })
                        })
                })
                .pageInfo(Storefront.PageInfoQueryDefinition { it.hasNextPage() })
        }

    fun getProductById(
        product_id: String,
        list_currency: List<Storefront.CurrencyCode>
    ): QueryRootQuery {
        return Storefront.query { root ->
            root.node(ID(product_id)) { rootnode ->
                rootnode.onProduct(
                    productQuery(list_currency)
                )
            }
        }
    }

    fun getProductByHandle(
        handle: String,
        list_currency: List<Storefront.CurrencyCode>
    ): QueryRootQuery {
        return Storefront.query { root ->
            root.productByHandle(
                handle,
                productQuery(list_currency)
            )
        }
    }

    fun getSearchProducts(
        keyword: String,
        cursor: String,
        list_currency: List<Storefront.CurrencyCode>
    ): QueryRootQuery {
        Log.d(TAG, "getSearchProducts: " + keyword)
        return Storefront.query { root ->
            root
                .products(
                    //   Storefront.QueryRootQuery.ProductsArgumentsDefinition { args -> args.query(keyword).first(30).sortKey(Storefront.ProductSortKeys.BEST_SELLING).reverse(false) }, productDefinition)
                    QueryRootQuery.ProductsArgumentsDefinition { args ->
                        product_list(
                            args,
                            cursor
                        ).query(keyword)
                    }, productDefinition(list_currency)
                )
        }
    }

    private fun product_list(
        args: QueryRootQuery.ProductsArguments?,
        cursor: String
    ): QueryRootQuery.ProductsArguments {
        var defination: QueryRootQuery.ProductsArguments? = null
        if (cursor == "nocursor") {
            defination = args!!.first(15)
        } else {
            defination = args!!.first(15).after(cursor)
        }
        return defination
    }

    fun getCustomerDetails(customeraccestoken: String): QueryRootQuery {
        return Storefront.query { root ->
            root
                .customer(
                    customeraccestoken
                ) { customerQuery ->
                    customerQuery
                        .firstName()
                        .lastName()
                        .email()
                        .id()
                }
        }
    }

    fun getOrderList(accesstoken: String?, cursor: String): QueryRootQuery {

        return Storefront.query { root ->
            root
                .customer(
                    accesstoken
                ) { customer ->
                    customer
                        .orders({ args -> order_list(args, cursor) }, { order ->
                            order
                                .edges({ edge ->
                                    edge
                                        .cursor()
                                        .node({ ordernode ->
                                            ordernode
                                                .customerUrl()
                                                .statusUrl()
                                                .name()
                                                .processedAt()
                                                .orderNumber()
                                                .fulfillmentStatus()
                                                .canceledAt()
                                                .cancelReason()
                                                .financialStatus()
                                                .totalRefundedV2 { _queryBuilder ->
                                                    _queryBuilder.amount().currencyCode()
                                                }.email()
                                                .phone()
                                                .totalPriceV2 { _queryBuilder ->
                                                    _queryBuilder.amount().currencyCode()
                                                }
                                                .shippingAddress { _queryBuilder ->
                                                    _queryBuilder.address1().address2().city()
                                                        .company().country().firstName()
                                                        .lastName()
                                                        .phone().zip().latitude().longitude()
                                                }.totalShippingPriceV2 { _queryBuilder ->
                                                    _queryBuilder.currencyCode().amount()
                                                }
                                                .totalTaxV2 { _queryBuilder ->
                                                    _queryBuilder.amount().currencyCode()
                                                }
                                                .subtotalPriceV2 { _queryBuilder ->
                                                    _queryBuilder.amount().currencyCode()
                                                }
                                                .lineItems({ arg -> arg.first(150) }, { item ->
                                                    item
                                                        .edges({ itemedge ->
                                                            itemedge
                                                                .node({ n ->
                                                                    n.title().quantity()
                                                                        .variant({ v ->
                                                                            v.product {
                                                                            }
                                                                            v.priceV2({ p ->
                                                                                p.amount()
                                                                                    .currencyCode()
                                                                            })
                                                                                .selectedOptions(
                                                                                    { select ->
                                                                                        select.name()
                                                                                            .value()
                                                                                    })
                                                                                .compareAtPriceV2(
                                                                                    { c ->
                                                                                        c.amount()
                                                                                            .currencyCode()
                                                                                    })
                                                                                .image(
                                                                                    Storefront.ImageQueryDefinition { it.originalSrc() })
                                                                        })
                                                                }
                                                                )
                                                        }
                                                        )
                                                }
                                                )
                                                .totalTaxV2({ tt ->
                                                    tt.amount().currencyCode()
                                                })
                                                .shippingAddress({ ship ->
                                                    ship.address1().address2().firstName()
                                                        .lastName().country().city().phone()
                                                        .province().zip()
                                                })
                                                .totalPriceV2({ tp ->
                                                    tp.amount().currencyCode()
                                                })
                                                .subtotalPriceV2({ st ->
                                                    st.amount().currencyCode()
                                                })
                                                .totalShippingPriceV2({ tsp ->
                                                    tsp.currencyCode().amount()
                                                })
                                        }
                                        )
                                }
                                )
                                .pageInfo(Storefront.PageInfoQueryDefinition { it.hasNextPage() })
                        }
                        )
                }
        }
    }

    private fun order_list(
        arg: Storefront.CustomerQuery.OrdersArguments,
        cursor: String
    ): Storefront.CustomerQuery.OrdersArguments {
        val definition: Storefront.CustomerQuery.OrdersArguments
        if (cursor == "nocursor") {
            definition = arg.first(10).reverse(true)
        } else {
            definition = arg.first(10).after(cursor).reverse(true)
        }
        return definition
    }

    fun getAddressList(accesstoken: String?, cursor: String): QueryRootQuery {
        return Storefront.query { root ->
            root
                .customer(accesstoken) { customer ->
                    customer
                        .addresses({ arg -> address_list(arg, cursor) }, { address ->
                            address
                                .edges({ edge ->
                                    edge
                                        .cursor()
                                        .node({ node ->
                                            node
                                                .firstName().lastName().company().address1()
                                                .address2().city().country().province().phone()
                                                .zip().formattedArea()
                                        }
                                        )
                                }
                                )
                                .pageInfo(Storefront.PageInfoQueryDefinition { it.hasNextPage() })
                        })
                }
        }
    }

    private fun address_list(
        arg: Storefront.CustomerQuery.AddressesArguments,
        cursor: String
    ): Storefront.CustomerQuery.AddressesArguments {
        val definitions: Storefront.CustomerQuery.AddressesArguments
        if (cursor == "nocursor")
            definitions = arg.first(10)
        else
            definitions = arg.first(10).after(cursor)

        return definitions
    }

    fun getProductByBarcode(
        barcode: String,
        list_currency: List<Storefront.CurrencyCode>
    ): QueryRootQuery {
        return Storefront.query { root ->
            root
                .products(
                    QueryRootQuery.ProductsArgumentsDefinition { args ->
                        args.query(
                            barcode
                        ).first(1).sortKey(Storefront.ProductSortKeys.BEST_SELLING)
                            .reverse(false)
                    }, productDefinition(list_currency)
                )
        }
    }

    fun pollCheckoutCompletion(paymentId: ID): QueryRootQuery {
        return query { rootQuery: QueryRootQuery ->
            rootQuery
                .node(
                    paymentId
                ) { nodeQuery: NodeQuery ->
                    nodeQuery
                        .onPayment { paymentQuery: PaymentQuery ->
                            paymentQuery
                                .checkout { checkoutQuery: CheckoutQuery ->
                                    checkoutQuery
                                        .order { orderQuery: OrderQuery ->
                                            orderQuery
                                                .processedAt()
                                                .orderNumber()
                                                .totalPrice()
                                        }
                                }
                                .errorMessage()
                                .ready()
                        }
                }
        }
    }

}
