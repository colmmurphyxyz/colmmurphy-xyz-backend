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

        packages.colmmurphyxyz-backend = pkgs.buildGradleApplication {
          pname = "colmmurphyxyz-backend";
          version = "1.0.0";
          src = ./.;
          meta = with build-gradle-application.lib; {
            description = "Backend server for api.colmmurphy.xyz";
          };
          buildTask = "bootJar installDist";
        };

        packages.default = self.packages.colmmurphyxyz-backend

        homeManagerModules.default = { config, lib, pkgs, ... }:
        let
          cfg = config.services.colmmurphyxyz-backend;
        in
        {
          options.services.colmmurphyxyz-backend = {
            enable = lib.mkEnableOption "colmmurphy.xyz Spring Boot backend";

            package = lib.mkOption {
              type = lib.types.package;
              default = self.packages.${pkgs.system}.default;
              description = "Backend package to run";
            };

            profile = lib.mkOption {
              type = lib.types.str;
              default = "prod";
            };

            javaOpts = lib.mkOption {
              type = lib.types.listOf lib.types.str;
              default = [];
            };

            environment = lib.mkOption {
              type = lib.types.attrsOf lib.types.str;
              default = { };
              description = "Extra environment variables";
            };
          };

          config = lib.mkIf cfg.enable {
            home.packages = [ cfg.package ];

            systemd.user.services.colmmurphyxyz-backend = {
              Unit = {
                Description = "colmmurphy.xyz Spring Boot backend";
                After = [ "network.target" ];
              };

              Service = {
                ExecStart =
                  "${cfg.package}/bin/colmmurphyxyz-backend";

                Restart = "always";
                RestartSec = 5;

                Environment =
                  [
                    "SPRING_PROFILES_ACTIVE=${cfg.profile}"
                    "JAVA_OPTS=${lib.concatStringsSep " " cfg.javaOpts}"
                  ]
                  ++ lib.mapAttrsToList (k: v: "${k}=${v}") cfg.environment;

                WorkingDirectory = "%h";
                StandardOutput = "journal";
                StandardError = "journal";
              };

              Install = {
                WantedBy = [ "default.target" ];
              };
            };
          };
        };
      }
    );
}
