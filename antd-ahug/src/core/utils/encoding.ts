import iconv from 'iconv-lite';
import fs from 'fs';
import path from 'path';

export function encodeGBK(str: string): Buffer {
  return iconv.encode(str, 'GBK');
}

export function decodeGBK(buffer: Buffer): string {
  return iconv.decode(buffer, 'GBK');
}

export function writeGBKFile(filePath: string, content: string): void {
  const dir = path.dirname(filePath);
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
  
  const gbkBuffer = encodeGBK(content);
  fs.writeFileSync(filePath, gbkBuffer);
}

export function readGBKFile(filePath: string): string {
  const buffer = fs.readFileSync(filePath);
  return decodeGBK(buffer);
}
