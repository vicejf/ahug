import { Routes, Route } from 'react-router-dom';
import MainLayout from './components/MainLayout';
import Dashboard from './pages/Dashboard';
import ConfigurationEditor from './pages/ConfigurationEditor';
import CodeGeneration from './pages/CodeGeneration';
import CodePreview from './pages/CodePreview';
import RecentFiles from './pages/RecentFiles';

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Dashboard />} />
        <Route path="config" element={<ConfigurationEditor />} />
        <Route path="generate" element={<CodeGeneration />} />
        <Route path="preview" element={<CodePreview />} />
        <Route path="recent" element={<RecentFiles />} />
      </Route>
    </Routes>
  );
}

export default App;
