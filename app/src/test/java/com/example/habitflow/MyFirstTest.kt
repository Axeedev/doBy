package com.example.habitflow


import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MyFirstTest : BehaviorSpec({

    Given("Простая математическая операция") {
        val a = 2
        val b = 3

        When("мы их складываем") {
            val result = a + b

            Then("результат должен быть 5") {
                result shouldBe 5
            }
        }
    }
})