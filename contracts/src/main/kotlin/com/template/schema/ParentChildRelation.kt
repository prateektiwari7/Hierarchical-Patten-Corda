package com.template.schema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.serialization.CordaSerializable
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.util.*
import javax.persistence.*

object Schema

@CordaSerializable
object SchemaV1 : MappedSchema(schemaFamily = Schema::class.java, version = 1, mappedTypes = listOf(PersistentParentToken::class.java, PersistentChildToken::class.java)) {


    @Entity
    @CordaSerializable
    @Table(name = "parent_data")
    class PersistentParentToken(
            @Column(name = "owner")
            var owner: String,

            @Column(name = "issuer")
            var issuer: String,

            @Column(name = "amount")
            var currency: Int,

            @Column(name = "linear_id")
            var linear_id: UUID,

            @OneToOne(fetch = FetchType.LAZY)
            @JoinColumns(JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id"), JoinColumn(name = "output_index", referencedColumnName = "output_index"))

            @OrderColumn
            @Cascade(CascadeType.PERSIST)
            var listOfPersistentChildTokens: PersistentChildToken?

    ) : PersistentState()

    @Entity
    @Suppress("unused")
    @Table(name = "child_data")
    class PersistentChildToken(

            @Column(name = "child_id", unique = true, nullable = false)
            var childId: Int? = null,

            @Column(name = "owner")
            var owner: String,

            @Column(name = "issuer")
            var issuer: String,

            @Column(name = "amount")
            var currency: Int,

            @Column(name = "linear_id")
            var linear_id: UUID,

            @OneToOne(fetch = FetchType.LAZY)
            @JoinColumns(JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id"), JoinColumn(name = "output_index", referencedColumnName = "output_index"))
            var parent: PersistentParentToken ? = null


    ) : PersistentState() {
        constructor() : this(0,"","",0, UUID.randomUUID(),parent = null)
    }
}