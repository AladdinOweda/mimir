// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "MimirIOS",
    platforms: [.iOS(.v16)],
    products: [
        .library(name: "MimirIOS", targets: ["MimirIOS"]),
    ],
    targets: [
        .target(name: "MimirIOS", path: "Sources/MimirIOS"),
    ]
)
