//
//  iosAppTests.swift
//  iosAppTests
//
//  Copyright © 2019 Sage Bionetworks. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// 1.  Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// 2.  Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// 3.  Neither the name of the copyright holder(s) nor the names of any contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission. No license is granted to the trademarks of
// the copyright holders even if such marks are included in this software.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

import XCTest
import AssessmentModel

class iosAppTests: XCTestCase {
    
    func testAssessmentGroup_StringDecoding() {
        let json = """
        {
            "type": "assessmentGroupInfo",
            "assessments": [
                {
                    "identifier":"list",
                    "type": "transformableAssessment",
                    "resourceName": "FormStep_List"
                },
                {
                    "identifier": "textfield",
                    "type": "transformableAssessment",
                    "resourceName": "FormStep_Textfield"
                }
            ]
        }
        """ // our data in native (JSON) format

        do {
            let loader = AssessmentGroupStringLoader(jsonString: json, bundle: .main)
            let group = try loader.decodeObject()
            try group.assessments.forEach { assessmentLoader in
                let assessment = try assessmentLoader.decodeObject()
                print(assessment)
            }
        } catch let err {
            XCTFail("Failed to decode files: \(err)")
        }
    }
    
    func testAnswerResult_Serialization() {
        let answerType = AnswerType.INTEGER()
        let jsonValue = answerType.jsonElementFor(value: 3)
        let result = AnswerResultObject(identifier: "foo", answerType: answerType, jsonValue: jsonValue)
        do {
            let dictionary = try result.jsonObject() as NSDictionary
            let expectedJson: NSDictionary = [
                            "identifier" : "foo",
                            "type" : "answer",
                            "answerType" : [
                                "type": "integer"
                            ],
                            "value" : 3
                        ]
            XCTAssertEqual(expectedJson, dictionary)
        } catch let err {
            XCTFail("Failed to encode: \(err)")
        }
    }
    
    func testJsonObject_Serialization() {
        let expectedJson: NSDictionary = [
            "identifier" : "foo",
            "type" : "answer",
            "answerType" : [
                "type": "integer"
            ],
            "value" : 3
        ]
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: expectedJson, options: [])
            guard let jsonString = String(data: jsonData, encoding: .utf8) else {
                XCTFail("Failed to convert data to UTF8 string")
                return
            }
            let jsonObject = try JsonElementDecoder(jsonString: jsonString).decodeObject()
            let encodedString = try JsonElementEncoder(jsonElement: jsonObject).encodeObject()
            guard let outputData = encodedString.data(using: .utf8) else {
                XCTFail("Failed to convert \(encodedString) to UTF8 data")
                return
            }
            let outputJson = try JSONSerialization.jsonObject(with: outputData, options: [])
            guard let outputDictionary = outputJson as? NSDictionary else {
                XCTFail("Failed to encode object as a dictionary")
                return
            }
            XCTAssertEqual(expectedJson, outputDictionary)
        } catch let err {
            XCTFail("Failed to encode: \(err)")
        }
        
    }
}

extension Result {
    func jsonObject() throws -> [String : Any]  {
        let encoding = try ResultEncoder(result: self).encodeObject()
        let data = encoding.data(using: .utf8)
        guard let jsonData = data else {
            let context = EncodingError.Context(codingPath: [], debugDescription: "Failed to get a UTF8 encoded string")
            throw EncodingError.invalidValue(self, context)
        }
        let json = try JSONSerialization.jsonObject(with: jsonData, options: [])
        guard let dictionary = json as? [String : Any] else {
            let context = EncodingError.Context(codingPath: [], debugDescription: "Failed to encode a dictionary for \(json)")
            throw EncodingError.invalidValue(json, context)
        }
        return dictionary
    }
}
