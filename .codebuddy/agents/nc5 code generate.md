---
name: nc5 code generate
description: 辅助项目代码生成
model: default
tools: list_dir, search_file, search_content, read_file, read_lints, replace_in_file, write_to_file, execute_command, mcp_get_tool_description, mcp_call_tool, create_rule, delete_file, preview_url, web_fetch, use_skill, web_search
agentMode: manual
enabled: true
enabledAutoRun: true
mcpTools: Windows CLI MCP Server
---
#工作区说明
ahug目录为项目目录
工作区src目录为velocity 模板生成的源码参考目录
#注意
禁止修改工作区src目录任何文件