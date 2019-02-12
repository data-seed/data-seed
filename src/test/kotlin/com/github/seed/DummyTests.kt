package com.github.seed

import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class DummyTests : StringSpec({

    "dummy test" {
        val value = "10"
        assertSoftly {
            value shouldBe "10"
        }
    }

})