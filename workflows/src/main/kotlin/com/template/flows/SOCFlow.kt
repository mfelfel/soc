package com.template.flows

import co.paralleluniverse.fibers.Suspendable

import com.template.states.SOCState
import com.template.contracts.SOCContract

import java.util.stream.Collectors


import net.corda.core.flows.*
import net.corda.core.utilities.ProgressTracker
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FlowSession
import net.corda.core.flows.FinalityFlow
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.contracts.requireThat
import net.corda.core.contracts.Command
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

// Replace Initiator's definition with:
@InitiatingFlow
@StartableByRPC
class SOCFlow(val industryParticipantIdentity: Party,
              val aerIdentity: Party,
              val title: String,
              val description: String,
              val location: String) : FlowLogic<Unit>() {

    /** The progress tracker provides checkpoints indicating the progress of
    the flow to observers. */
    override val progressTracker = ProgressTracker()

    /** The flow logic is encapsulated within the call() method. */
    @Suspendable
    override fun call() {
        // We retrieve the notary identity from the network map.
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        // We create the transaction components.
        val outputState = SOCState(
                title,
                description,
                location,
                industryParticipantIdentity,
                ourIdentity,
                aerIdentity
        )

        val command = Command(
                SOCContract.Create(),
                listOf(
                        industryParticipantIdentity.owningKey,
                        ourIdentity.owningKey,
                        aerIdentity.owningKey
                )
        )

        // We create a transaction builder and add the components.
        val txBuilder = TransactionBuilder(notary = notary)
                .addOutputState(outputState, SOCContract.ID)
                .addCommand(command)

        // Verifying the transaction.
        txBuilder.verify(serviceHub)

        // We sign the transaction.
        val signedTx = serviceHub.signInitialTransaction(txBuilder)

        // Creating a session with the other party.
        val industryParticipantIdentitySession = initiateFlow(industryParticipantIdentity)

        // Creating a session with the other party.
        val aerIdentitySession = initiateFlow(aerIdentity)

        // Optain the parties signatures.
        val fullySignedTx = subFlow(CollectSignaturesFlow(signedTx, listOf(industryParticipantIdentitySession, aerIdentitySession), CollectSignaturesFlow.tracker()))

        // We finalise the transaction and then send it to the counterparty.
        subFlow(FinalityFlow(fullySignedTx,
                industryParticipantIdentitySession,
                aerIdentitySession))
    }
}


// Replace Responder's definition with:
@InitiatedBy(SOCFlow::class)
class SOCFlowResponder(private val otherPartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(ReceiveFinalityFlow(otherPartySession))
    }
}


/*
import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import com.template.IOUContract
import com.template.states.IOUState
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.ReceiveFinalityFlow
import net.corda.core.flows.FlowSession
import net.corda.core.contracts.requireThat
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.transactions.SignedTransaction

*/


