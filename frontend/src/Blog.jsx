import React, { useEffect, useState } from 'react';
import axios from 'axios';

export default function Blog() {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    axios.get('/api/blog').then(res => setPosts(res.data));
  }, []);

  return (
    <div className="p-4">
      <h2 className="text-2xl font-bold mb-4">Blog</h2>
      {posts.map(p => (
        <div key={p.id} className="mb-4">
          <h3 className="font-semibold text-xl">{p.title}</h3>
          <p>{p.content}</p>
        </div>
      ))}
    </div>
  );
}
