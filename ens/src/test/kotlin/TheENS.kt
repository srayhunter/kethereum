
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.kethereum.contract.abi.types.model.types.AddressETHType
import org.kethereum.crypto.test_data.TEST_ADDRESSES
import org.kethereum.eip137.ENSName
import org.kethereum.ens.ENS
import org.kethereum.ens.ENS_DEFAULT_CONTRACT_ADDRESS
import org.kethereum.model.Transaction
import org.kethereum.rpc.EthereumRPC
import org.kethereum.rpc.model.StringResultResponse
import org.walleth.khex.toHexString

@ExtendWith(MockKExtension::class)
class TheENS {

    @MockK
    lateinit var mockRPC: EthereumRPC

    @Test
    fun testGetResolver() {
        every {
            mockRPC.call(any())
        } returns StringResultResponse(AddressETHType.ofNativeKotlinType(TEST_ADDRESSES.first()).paddedValue.toHexString())

        val ens = ENS(mockRPC, ENS_DEFAULT_CONTRACT_ADDRESS)

        assertThat(ens.getResolver(ENSName("foo.eth"))).isEqualTo(TEST_ADDRESSES.first())

        val slot = slot<Transaction>()
        verify(exactly = 1) {
            mockRPC.call(capture(slot))
        }

        confirmVerified(mockRPC)
        assertThat(slot.captured.to).isEqualTo(ENS_DEFAULT_CONTRACT_ADDRESS)
    }


    @Test
    fun testGetAddress() {
        every {
            mockRPC.call(any())
        } returns StringResultResponse(AddressETHType.ofNativeKotlinType(TEST_ADDRESSES.last()).paddedValue.toHexString())

        val ens = ENS(mockRPC, ENS_DEFAULT_CONTRACT_ADDRESS)

        assertThat(ens.getAddress(ENSName("foo.eth"))).isEqualTo(TEST_ADDRESSES.last())

        val slot = slot<Transaction>()
        verify(exactly = 2) {
            mockRPC.call(capture(slot))
        }

        confirmVerified(mockRPC)
        assertThat(slot.captured.to).isEqualTo(TEST_ADDRESSES.last())

    }
}