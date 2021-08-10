package br.com.zupacademy.rayllanderson.pix.register

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

internal class PixKeyTypeTest {

    @Nested
    inner class CPF {

        @Test
        fun `should be valid when key is well formatted`() {
            val keyType = KeyTypeRequest.CPF

            assertTrue(keyType.validate("05684368428"))
        }

        @Test
        fun `shouldn't be valid when key is empty or null`() {
            val keyType = KeyTypeRequest.CPF

            assertFalse(keyType.validate(""))
            assertFalse(keyType.validate(null))
        }

        @Test
        fun `shouldn't be valid when key contains letter`() {
            val keyType = KeyTypeRequest.CPF

            assertFalse(keyType.validate("0568436842ç"))
            assertFalse(keyType.validate("whatever197"))
        }
    }

    @Nested
    inner class EMAIL {

        @Test
        fun `should be valid when key is well formatted`() {
            val keyType = KeyTypeRequest.EMAIL

            assertTrue(keyType.validate("kaguya@sama.com"))
            assertTrue(keyType.validate("mikasa@ackerman.com"))
        }

        @Test
        fun `shouldn't be valid when key is not well formatted`() {
            val keyType = KeyTypeRequest.EMAIL

            assertFalse(keyType.validate("kaguya-sama.com"))
            assertFalse(keyType.validate("kaguya@samacom"))
        }

        @Test
        fun `shouldn't be valid when key is empty or null`() {
            val keyType = KeyTypeRequest.EMAIL

            assertFalse(keyType.validate(""))
            assertFalse(keyType.validate(null))
        }
    }

    @Nested
    inner class PHONE {

        @Test
        fun `should be valid when key is well formatted`() {
            val keyType = KeyTypeRequest.PHONE

            assertTrue(keyType.validate("+5598993648963"))
        }

        @Test
        fun `shouldn't be valid when key is not well formatted`() {
            val keyType = KeyTypeRequest.PHONE

            assertFalse(keyType.validate("5598993648963"))
        }

        @Test
        fun `shouldn't be valid when key is empty or null`() {
            val keyType = KeyTypeRequest.PHONE

            assertFalse(keyType.validate(""))
            assertFalse(keyType.validate(null))
        }
    }

    @Nested
    inner class RANDOM {

        @Test
        fun `should be valid when key is empty or null`() {
            val keyType = KeyTypeRequest.RANDOM

            assertTrue(keyType.validate(""))
            assertTrue(keyType.validate(null))
        }

        @Test
        fun `shouldn't be valid when key is present`() {
            val keyType = KeyTypeRequest.RANDOM

            assertFalse(keyType.validate(UUID.randomUUID().toString()))
        }
    }
}