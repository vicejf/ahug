import ejs from 'ejs';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

class TemplateEngine {
  constructor() {
    this.templatesDir = path.join(__dirname, '../templates');
  }

  async render(templateName, data) {
    const templatePath = path.join(this.templatesDir, templateName);
    
    if (!fs.existsSync(templatePath)) {
      throw new Error(`Template not found: ${templatePath}`);
    }

    const template = await fs.promises.readFile(templatePath, 'utf-8');
    return ejs.render(template, data, {
      views: [this.templatesDir],
      root: this.templatesDir
    });
  }

  async renderToFile(templateName, data, outputPath) {
    const content = await this.render(templateName, data);
    
    const dir = path.dirname(outputPath);
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }

    await fs.promises.writeFile(outputPath, content, 'GBK');
  }
}

export default new TemplateEngine();
