import Link from "next/link";
import { getNewsByTeamName } from "@/lib/api/endpoints";
import type { NewsArticle } from "@/lib/api/types";
import { NewsArticleCard } from "@/components/news/news-article-card";
import { EmptyState } from "@/components/ui/empty-state";
import { filterNewsForTeam } from "@/lib/news";

const TEAM_NEWS_DISPLAY_LIMIT = 6;

export async function TeamRelatedNews({ teamName }: { teamName: string }) {
  let articles: NewsArticle[] = [];

  try {
    const news = await getNewsByTeamName(teamName);
    articles = filterNewsForTeam(
      teamName,
      news?.articles ?? [],
      TEAM_NEWS_DISPLAY_LIMIT,
    );
  } catch {
    /* hide section errors — squad/fixtures are primary content */
  }

  return (
    <section className="mt-10">
      <div className="mb-4 flex items-end justify-between gap-4">
        <h2 className="text-2xl font-semibold text-white">Related news</h2>
        <Link
          href="/news"
          className="text-sm text-emerald-400 hover:text-emerald-300"
        >
          All news →
        </Link>
      </div>

      {articles.length === 0 ? (
        <EmptyState
          title="No headlines for this team yet"
          description="Team-specific news is fetched on a schedule. Check back after the next sync."
        />
      ) : (
        <div className="grid gap-3">
          {articles.map((article) => (
            <NewsArticleCard
              key={article.url}
              article={article}
              compact
            />
          ))}
        </div>
      )}
    </section>
  );
}
