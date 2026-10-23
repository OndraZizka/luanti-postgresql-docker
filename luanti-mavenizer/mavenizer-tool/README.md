# mavenizer-tool

Turns [ContentDB](https://content.luanti.org) packages into Maven `zip` artifacts and (optionally) deploys them
to Maven Central via the Sonatype Central Portal.

## Coordinates

| ContentDB          | Maven                                                  |
|--------------------|--------------------------------------------------------|
| mod `author/name`  | `ch.zizka.luanti.mods.<author>:<name>:<releaseId>`     |
| game `author/name` | `ch.zizka.luanti.games.<author>:<name>:<releaseId>`    |

The version is the ContentDB release id (see the `.N` suffix below). Characters outside `[A-Za-z0-9_.-]` become `_`.

## Mavenizing

```
scripts/mavenize.sh [--local] <targets>
```

The script compiles the tool, builds the classpath and runs `ch.zizka.luanti.mavenizer.MainKt`.

Targets:
- `--game=ryvnf/mineclonia` - all mods supporting a game
- `author/name` - latest release of a package
- `author/name/releaseId` - a specific release

For each package a POM is generated in `generated-mods-project/<author>/<name>/` and built. The POM carries the
ContentDB metadata: description, license, developers/maintainers, issue tracker, SCM, and `luanti.*` properties
(dev state, release date, tags, website, forum, donate URL, compatible games, mod.conf dependency names).
The artifact is a zip that downloads the release from ContentDB.

## Updating

`--upgrade-existing=<source>` mavenizes new ContentDB releases (with their dependencies) of packages which are already
mavenized. `<source>` is where the list of mavenized packages comes from:
- `localRepo` - the packages installed under `ch.zizka.luanti.*` in `~/.m2`
- `mavenCentral` - the packages published to Central (read from the `repo1.maven.org` directory listings)
- `modsForGame` - the packages in `~/.m2` which support the game given by `--game=<author>/<name>`

Add `--dry-run` to only list what would be upgraded, e.g. `scripts/mavenize.sh --upgrade-existing=mavenCentral --dry-run`.
Add `--local` to install into `~/.m2` instead of deploying. Packages are matched through the ContentDB URL in their POMs.
Central's mirror can lag behind a fresh deploy.

## Deploying

- `--local` - `mvn install` into `~/.m2` (GPG skipped). Use this to try things out.
- without it - `mvn deploy -P ossrh`, GPG-signed, published to Central. Set `MAVEN_GPG_PASSPHRASE` in the
  environment (never in `settings.xml`).

Ready-made scripts are in `../scripts/`: `deploy-mineclonia-mods.sh`, `deploy-family-server-mods.sh`
(reads `family-server-mods.txt`). Both accept `--local`.

**Central is immutable**: a published version can never be overwritten.

## Dependencies

Dependencies come from the ContentDB dependency API (`mod.conf` only has names).

- **Mods**: required and optional mod dependencies become normal Maven `<dependency>` entries (type `zip`;
  optional ones are `<optional>true</optional>`) at the dependency's latest release. A mod provided by a game the
  package supports needs no entry. If several packages provide a name, the one named like the mod wins; anything
  still ambiguous is listed in the `luanti.depends.unresolved` property.
- **Not-yet-mavenized dependencies**: a package is deferred until its dependencies exist (in this run or in
  `~/.m2`). If the tool can't progress, it lists what is missing in
  `generated-mods-project/missing-dependencies.txt` (`author/name/release` lines). Feed that file's contents back
  as targets to mavenize them, then re-run the rest.
- **Games**: mavenized as *dummy* packages (`ch.zizka.luanti.games.<author>`): the zip holds only a
  `PLACEHOLDER.txt` saying the game itself is not included, to avoid cluttering Central. A mod lists each compatible
  game as a regular (non-optional) dependency inside its own profile, activated with
  `-Dluanti.game=<author>/<name>`. Being in a profile, a game that is not installed never breaks the mod's build.
  All compatible games are also in the `luanti.games.compatible` property.
- Texture packs (`txp`) are supported as a package type too (`ch.zizka.luanti.texturepacks.<author>`).

## The `.N` version suffix

If `<releaseId>` of a package is already on Maven Central (checked via `repo1.maven.org`), the tool generates
`<releaseId>.1` instead, since the old POM cannot be replaced and lacks the metadata/dependencies. The same rule
is applied to dependency versions written into POMs. Maven orders `37835 < 37835.1 < 37836`. New ContentDB
releases get the plain id again, so the suffixes fade out over time. Central's mirror can lag after a fresh
deploy; re-run later if a deploy fails with "already exists".
