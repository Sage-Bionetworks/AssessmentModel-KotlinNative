// swift-tools-version:5.3
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AssessmentModel",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v13),
        .macOS(.v11),
    ],
    products: [
        // Products define the executables and libraries a package produces, and make them visible to other packages.
        .library(
            name: "AssessmentModel",
            targets: ["AssessmentModel",
                      "AssessmentModelUI",
            ]),
    ],
    dependencies: [
        .package(name: "MobilePassiveData",
                 url: "https://github.com/Sage-Bionetworks/MobilePassiveData-SDK.git",
                 from: "1.1.0"),
        .package(name: "JsonModel",
                 url: "https://github.com/Sage-Bionetworks/JsonModel-Swift.git",
                 from: "1.2.0"),
    ],
    targets: [
        // Targets are the basic building blocks of a package. A target can define a module or a test suite.
        // Targets can depend on other targets in this package, and on products in packages this package depends on.
        .binaryTarget(name: "AssessmentModel",
                      path: "SwiftPackage/Binaries/AssessmentModel.xcframework"),
        .testTarget(
            name: "AssessmentModelTests",
            dependencies: ["AssessmentModel"],
            path: "SwiftPackage/Tests/AssessmentModelTests",
            resources: [ .process("Resources")]),
        
        .target(name: "AssessmentModelUI",
                dependencies: [
                    "AssessmentModel",
                    .product(name: "MobilePassiveData", package: "MobilePassiveData"),
                    "JsonModel",
                ],
                path: "SwiftPackage/Sources/AssessmentModelUI",
                resources: [ .process("Resources")]),
    ]
)
