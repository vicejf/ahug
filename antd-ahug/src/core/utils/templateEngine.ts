import ejs from 'ejs';
import path from 'path';
import { fileURLToPath } from 'url';
import { writeGBKFile } from './encoding';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export interface TemplateContext {
  bill: any;
  [key: string]: any;
}

export class TemplateEngine {
  private templatesDir: string;

  constructor(templatesDir: string = path.join(__dirname, '../templates')) {
    this.templatesDir = templatesDir;
  }

  async render(templatePath: string, context: TemplateContext): Promise<string> {
    const fullPath = path.join(this.templatesDir, templatePath);
    return await ejs.renderFile(fullPath, context, {
      async: true
    });
  }

  async renderAndWrite(templatePath: string, outputPath: string, context: TemplateContext): Promise<void> {
    const content = await this.render(templatePath, context);
    writeGBKFile(outputPath, content);
  }
}

export default TemplateEngine;
