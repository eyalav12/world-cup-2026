import type { NextConfig } from "next";

const apiBase = process.env.API_BASE_URL ?? "http://localhost:8083";
const agentBase = process.env.AGENT_API_URL ?? "http://localhost:8000";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [
      { protocol: "https", hostname: "crests.football-data.org", pathname: "/**" },
      { protocol: "https", hostname: "flagcdn.com", pathname: "/**" },
    ],
  },
  async rewrites() {
    return {
      beforeFiles: [
        {
          source: "/api/agent/:path*",
          destination: `${agentBase}/agent/:path*`,
        },
      ],
      afterFiles: [
        {
          source: "/api/backend/:path*",
          destination: `${apiBase}/:path*`,
        },
      ],
    };
  },
};

export default nextConfig;
