package com.github.seed

import io.kotlintest.assertSoftly
import io.kotlintest.matchers.maps.shouldNotContainKey
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class DataRecordTests : StringSpec({

    "read record with all values present with respective data type string, integer, number and boolean" {
        val columns = arrayOf("City Code","City Name","State","Rank[Int]","Active[Boolean]","Latitude[Number]","Longitude[Number]")
        val values = arrayOf("022","Mumbai","Maharastra","100","false","18.52","73.86")
        val record = DataRecord().build(columns,values)

        assertSoftly {
            record["City Code"] shouldBe "022"
            record["City Name"] shouldBe "Mumbai"
            record["State"] shouldBe "Maharastra"
            record["Rank"] shouldBe 100
            record["Latitude"] shouldBe 18.52f
            record["Longitude"] shouldBe 73.86f
        }
    }

    "read record with all few values absent" {
        val columns = arrayOf("City Code","City Name","State","Rank[Int]")
        val values = arrayOf("022","","Maharastra","")
        val record = DataRecord().build(columns,values)

        assertSoftly {
            record["City Code"] shouldBe "022"
            record.shouldNotContainKey("City Name")
            record["State"] shouldBe "Maharastra"
            record shouldNotContainKey "Rank"
        }
    }


    "read record with values having extra spaces at the end" {
        val columns = arrayOf("City Code ","City Name"," State","Rank[Int]")
        val values = arrayOf(" 022","Mumbai ","Maharastra","")
        val record = DataRecord().build(columns,values)

        assertSoftly {
            record["City Code"] shouldBe "022"
            record["City Name"] shouldBe "Mumbai"
            record["State"] shouldBe "Maharastra"
            record shouldNotContainKey "Rank"
        }
    }
})