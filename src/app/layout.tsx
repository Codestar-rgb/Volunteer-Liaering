import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { Toaster } from "@/components/ui/toaster";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "SubspaceParasite — Download",
  description: "Download SubspaceParasite mod for Minecraft 1.20.1 Forge. Ported from Scape and Run: Parasites.",
  keywords: ["SubspaceParasite", "Minecraft", "Forge", "1.20.1", "SRP", "Parasites", "Mod"],
  authors: [{ name: "SubspaceParasite Team" }],
  icons: {
    icon: "https://z-cdn.chatglm.cn/z-ai/static/logo.svg",
  },
  openGraph: {
    title: "SubspaceParasite — Download",
    description: "Download SubspaceParasite mod for Minecraft 1.20.1 Forge",
    type: "website",
  },
  twitter: {
    card: "summary",
    title: "SubspaceParasite — Download",
    description: "Download SubspaceParasite mod for Minecraft 1.20.1 Forge",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased bg-background text-foreground`}
      >
        {children}
        <Toaster />
      </body>
    </html>
  );
}
