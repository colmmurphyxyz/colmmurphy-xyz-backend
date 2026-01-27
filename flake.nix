{
  description = "colmmurphy.xyz backend";
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.11";
    build-gradle-application.url = "github:raphiz/buildGradleApplication";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      build-gradle-application,
      flake-utils,
      ...
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [ build-gradle-application.overlays.default ];
          config.allowUnfree = true;
        };
        java = pkgs.jdk21_headless;
        gradle = pkgs.gradle_9;
        kotlin = pkgs.kotlin;
        intellij = pkgs.jetbrains.idea;
      in
      {
        devShells.default = pkgs.mkShell {
          name = "spring boot + kotlin dev";
          buildInputs = [
            gradle
            intellij
            java
            kotlin
          ];
          shellHook = ''
            export JAVA_HOME=$(dirname $(dirname $(which java)))

            export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:${
              pkgs.lib.makeLibraryPath [
                kotlin
                pkgs.libGL
                pkgs.xorg.libX11
                pkgs.fontconfig
              ]
            };

            echo "Activated"
          '';
        };

        packages.default = pkgs.buildGradleApplication {
          pname = "colmmurphyxyz-backend";
          version = "1.0.0";
          src = ./.;
          meta = with build-gradle-application.lib; {
            description = "Backend server for api.colmmurphy.xyz";
          };
          buildTask = "bootJar installDist";
        };
      }
    );
}
