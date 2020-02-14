package com.template.states

import com.template.contracts.Parent_Child_Contract
import com.template.schema.SchemaV1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

@BelongsToContract(Parent_Child_Contract::class)
data class Parent_Child_State(val owner: String,
                              val issuer: String,
                              val currency: Int,
                              override val linearId: UniqueIdentifier = UniqueIdentifier(), override val participants: List<AbstractParty>,
                              val listOfPersistentChildTokens : SchemaV1.PersistentChildToken ?

):
        LinearState, QueryableState {
    /** The public keys of the involved parties. */
  //  override val participants: List<AbstractParty> get() = listOf(loginto)


    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is SchemaV1 -> SchemaV1.PersistentParentToken(
            this.owner, this.issuer,this.currency, linear_id = linearId.id, listOfPersistentChildTokens = listOfPersistentChildTokens)
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(SchemaV1)

}