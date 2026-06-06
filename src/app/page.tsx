"use client";

import { useState, useEffect } from "react";
import { Download, FileArchive, Shield, Bug, ChevronRight } from "lucide-react";

function formatSize(bytes: number): string {
  if (bytes >= 1073741824) return (bytes / 1073741824).toFixed(2) + " GB";
  if (bytes >= 1048576) return (bytes / 1048576).toFixed(1) + " MB";
  if (bytes >= 1024) return (bytes / 1024).toFixed(1) + " KB";
  return bytes + " B";
}

export default function Home() {
  const [fileSize, setFileSize] = useState<number | null>(null);
  const [downloading, setDownloading] = useState(false);

  useEffect(() => {
    fetch("/api/download", { method: "HEAD" })
      .then((res) => {
        const len = res.headers.get("content-length");
        if (len) setFileSize(parseInt(len, 10));
      })
      .catch(() => {});
  }, []);

  const handleDownload = () => {
    setDownloading(true);
    const a = document.createElement("a");
    a.href = "/api/download";
    a.download = "SubspaceParasite-1.0.0.jar";
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    setTimeout(() => setDownloading(false), 2000);
  };

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <main className="flex-1 flex items-center justify-center p-6">
        <div className="w-full max-w-lg space-y-8">
          {/* Logo & Title */}
          <div className="text-center space-y-3">
            <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-primary/10 text-primary mb-2">
              <Bug className="w-8 h-8" />
            </div>
            <h1 className="text-2xl font-bold tracking-tight text-foreground">
              SubspaceParasite
            </h1>
            <p className="text-sm text-muted-foreground">
              Scape and Run: Parasites — Minecraft 1.20.1 Forge Mod
            </p>
          </div>

          {/* Download Card */}
          <div className="rounded-xl border bg-card p-6 space-y-5 shadow-sm">
            <div className="flex items-start gap-4">
              <div className="flex-shrink-0 w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center text-primary">
                <FileArchive className="w-6 h-6" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-medium text-sm text-foreground truncate">
                  SubspaceParasite-1.0.0.jar
                </p>
                <p className="text-xs text-muted-foreground mt-0.5">
                  {fileSize ? formatSize(fileSize) : "Loading..."} · Forge 47.3.0 · Java 17
                </p>
              </div>
            </div>

            <button
              onClick={handleDownload}
              disabled={downloading}
              className="w-full flex items-center justify-center gap-2 rounded-lg bg-primary text-primary-foreground px-4 py-2.5 text-sm font-medium hover:bg-primary/90 transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
            >
              <Download className="w-4 h-4" />
              {downloading ? "Starting..." : "Download JAR"}
            </button>
          </div>

          {/* Info */}
          <div className="space-y-2.5">
            <p className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
              Install
            </p>
            <div className="rounded-lg border bg-card divide-y">
              {[
                "Install Minecraft Forge 47.3.0 (1.20.1)",
                "Place the JAR in your .minecraft/mods folder",
                "Launch the game with the Forge profile",
              ].map((step, i) => (
                <div
                  key={i}
                  className="flex items-center gap-3 px-4 py-3 text-sm text-foreground"
                >
                  <span className="flex-shrink-0 w-5 h-5 rounded-full bg-primary/10 text-primary text-xs font-medium flex items-center justify-center">
                    {i + 1}
                  </span>
                  {step}
                </div>
              ))}
            </div>
          </div>

          {/* Requirements */}
          <div className="flex flex-wrap items-center justify-center gap-x-4 gap-y-1 text-xs text-muted-foreground">
            <span className="inline-flex items-center gap-1">
              <Shield className="w-3 h-3" />
              Minecraft 1.20.1
            </span>
            <span className="inline-flex items-center gap-1">
              <ChevronRight className="w-3 h-3" />
              Forge 47.3.0
            </span>
            <span className="inline-flex items-center gap-1">
              <ChevronRight className="w-3 h-3" />
              Java 17+
            </span>
          </div>
        </div>
      </main>

      <footer className="mt-auto py-4 text-center text-xs text-muted-foreground border-t">
        SubspaceParasite — Ported from SRP 1.12.2
      </footer>
    </div>
  );
}
