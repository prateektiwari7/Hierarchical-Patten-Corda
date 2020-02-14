package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.sun.xml.internal.ws.developer.Serialization
import com.template.contracts.Parent_Child_Contract
import com.template.schema.SchemaV1
import com.template.states.Parent_Child_State
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

@InitiatingFlow
@StartableByRPC
@CordaSerializable
class TestParentChild(val owner : Party, val bal: Int)  : FlowLogic<SignedTransaction>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    @Serialization
    override fun call() : SignedTransaction{
        val notary = serviceHub.networkMapCache.notaryIdentities[0]
        var listOfPersistentChildTokens : SchemaV1.PersistentChildToken ? = null


        listOfPersistentChildTokens = SchemaV1.PersistentChildToken(0,ourIdentity.toString(),owner.toString(),bal,UUID.randomUUID(),null)


        var state = Parent_Child_State(ourIdentity.toString(),owner.toString(),bal, UniqueIdentifier(UUID.randomUUID().toString()), listOf(owner),listOfPersistentChildTokens)

        var Command = Command(Parent_Child_Contract.Commands.Action(),state.participants.map { it.owningKey })

        val txBuilder = TransactionBuilder(notary = notary)
                .addOutputState(state, Parent_Child_Contract.ID)
                .addCommand(Command)

        txBuilder.verify(serviceHub)

        val partSignedTx = serviceHub.signInitialTransaction(txBuilder)

        val otherPartySession = initiateFlow(owner)

        val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, setOf(otherPartySession)))

        return subFlow(FinalityFlow(fullySignedTx))

    }
}

@InitiatedBy(TestParentChild::class)
class TestParentChild_responder(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call() : SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat{
                val output = stx.tx.outputs.single().data

            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow)
        return subFlow(ReceiveFinalityFlow(counterpartySession, txWeJustSignedId.id))

    }
}
