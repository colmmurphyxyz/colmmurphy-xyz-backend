{
  description = "Developer Environment.";
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.05";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils}:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
        };
      in {
        devShells.default = pkgs.mkShell {
          name = "spring boot + kotlin dev";
          buildInputs = with pkgs; [
            git
            gradle
            jdk17
            kotlin
          ];
          shellHook = ''
            export JAVA_HOME=$(dirname $(dirname $(which java)))
            echo "Activated"
          '';
        };
      }
    );
}

