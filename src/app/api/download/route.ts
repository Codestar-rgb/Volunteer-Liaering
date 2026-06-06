import { NextRequest, NextResponse } from "next/server";
import { readFile, stat } from "fs/promises";
import path from "path";

const JAR_PATH = path.join(process.cwd(), "download", "SubspaceParasite-1.0.0.jar");

export async function GET(request: NextRequest) {
  try {
    const fileStat = await stat(JAR_PATH);
    const fileBuffer = await readFile(JAR_PATH);

    const range = request.headers.get("range");

    if (range) {
      const fileSize = fileStat.size;
      const parts = range.replace(/bytes=/, "").split("-");
      const start = parseInt(parts[0], 10);
      const end = parts[1] ? parseInt(parts[1], 10) : fileSize - 1;
      const chunkSize = end - start + 1;

      const chunk = fileBuffer.subarray(start, end + 1);

      return new NextResponse(chunk, {
        status: 206,
        headers: {
          "Content-Range": `bytes ${start}-${end}/${fileSize}`,
          "Accept-Ranges": "bytes",
          "Content-Length": chunkSize.toString(),
          "Content-Type": "application/java-archive",
          "Content-Disposition": 'attachment; filename="SubspaceParasite-1.0.0.jar"',
        },
      });
    }

    return new NextResponse(fileBuffer, {
      status: 200,
      headers: {
        "Content-Type": "application/java-archive",
        "Content-Length": fileStat.size.toString(),
        "Content-Disposition": 'attachment; filename="SubspaceParasite-1.0.0.jar"',
        "Accept-Ranges": "bytes",
      },
    });
  } catch {
    return NextResponse.json(
      { error: "File not found" },
      { status: 404 }
    );
  }
}
