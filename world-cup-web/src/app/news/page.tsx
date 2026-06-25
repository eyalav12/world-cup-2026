import Link from "next/link";
import { NewsArticleCard } from "@/components/news/news-article-card";
import { EmptyState } from "@/components/ui/empty-state";
import { ErrorBanner } from "@/components/ui/error-banner";
import { getGeneralNews } from "@/lib/api/endpoints";
import { ApiError } from "@/lib/api/client";
import type { NewsArticle } from "@/lib/api/types";

export const metadata = { title: "News" };

export default async function NewsPage() {
  let articles: NewsArticle[] = [];
  let apiError: string | null = null;
  let cacheEmpty = false;

  try {
    const news = await getGeneralNews();
    if (news === null) {
      cacheEmpty = true;
    } else {
      articles = news.articles ?? [];
    }
  } catch (e) {
    apiError =
      e instanceof ApiError
        ? e.message
        : "Could not load news right now. Please try again later.";
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-3xl font-bold text-white">News</h1>
      <p className="mt-2 text-emerald-100/70">
        Latest World Cup headlines, squad updates, and tournament stories.
      </p>

      {apiError ? (
        <div className="mt-6">
          <ErrorBanner message={apiError} />
        </div>
      ) : null}

      <div className="mt-8">
        {cacheEmpty && !apiError ? (
          <EmptyState
            title="News is warming up"
            description="Headlines are fetched on a schedule and cached. Check back in a few minutes after the backend sync runs."
          />
        ) : null}

        {!cacheEmpty && !apiError && articles.length === 0 ? (
          <EmptyState
            title="No articles found"
            description="Try again later — the news feed refreshes throughout the day."
          />
        ) : null}

        {articles.length > 0 ? (
          <div className="grid gap-4">
            {articles.map((article) => (
              <NewsArticleCard key={article.url} article={article} />
            ))}
          </div>
        ) : null}
      </div>

      <p className="mt-8 text-sm text-emerald-100/60">
        Want quick answers? Try the{" "}
        <Link href="/agent" className="text-emerald-400 hover:underline">
          AI assistant
        </Link>{" "}
        for match previews and team insights.
      </p>
    </div>
  );
}
