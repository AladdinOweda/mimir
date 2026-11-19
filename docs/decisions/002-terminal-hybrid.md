# ADR 002 — Terminal Hybrid
**Status:** Accepted
**Decision:** Hybrid terminal runtime (native default + optional Termux Bootstrap).
**UI:** xterm.js mobile/web; JediTerm desktop.
**v4.1 extension:** AI Terminal (Chat default / Classic / Split), Auto-invoke, 5 safety layers, Assisted automation default.
**Service:** TerminalService + Foreground Service; all shell work goes through it.
