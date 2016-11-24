package keyvent

import org.jetbrains.spek.api.Spek
import java.time.LocalDateTime
import kotlin.test.assertEquals

class HandleCustomerCommandsSpec : Spek() {
    init {
        given("An empty Customer snapshot and a create command") {
            val snapshot = Snapshot(Customer(), Version(0))
            val cmd = CreateCustomerCmd(customerId = CustomerId())
            val uow: CustomerUnitOfWork? = handleCustomerCommands.invoke(snapshot, cmd, applyEventOnCustomer)
            assertEquals(uow?.customerCommand, cmd)
            assertEquals(uow?.version, Version(1))
            assertEquals(uow?.events!!.first(), CustomerCreated(cmd.customerId))
        }
        given("A fresh non active Customer snapshot with version 1 and an activate command") {
            val localDatTime = LocalDateTime.now()
            val snapshot = Snapshot(Customer(customerId = CustomerId(), name="customer1", active = false, activatedSince = localDatTime), Version(1))
            val cmd = ActivateCustomerCmd(CommandId(), customerId = snapshot.eventSourced.customerId!!, date = localDatTime)
            val uow: CustomerUnitOfWork? = handleCustomerCommands.invoke(snapshot, cmd, applyEventOnCustomer)
            assertEquals(uow?.customerCommand, cmd)
            assertEquals(uow?.version, Version(2))
            assertEquals(uow?.events?.first(), CustomerActivated(localDatTime))
        }
    }
}
