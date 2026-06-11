import type { NewsArticle } from "@/lib/api/types";
import { formatNewsDate } from "@/lib/news";

export function NewsArticleCard({
  article,
  compact = false,
}: {
  article: NewsArticle;
  compact?: boolean;
}) {
  const sourceName = article.source?.name;
  const imageClass = compact
    ? "h-16 w-20 rounded-lg object-cover sm:h-20 sm:w-28"
    : "h-20 w-24 rounded-xl object-cover sm:h-24 sm:w-32";

  return (
    <a
      href={article.url}
      target="_blank"
      rel="noopener noreferrer"
      className="group flex gap-3 rounded-2xl border border-white/10 bg-white/5 p-3 transition hover:border-emerald-400/40 hover:bg-white/[0.08] sm:gap-4 sm:p-4"
    >
      {article.urlToImage ? (
        <div className="shrink-0">
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img
            src={article.urlToImage}
            alt=""
            className={imageClass}
            loading="lazy"
          />
        </div>
      ) : null}

      <div className="min-w-0 flex-1">
        <div className="mb-1 flex flex-wrap items-center gap-2 text-xs text-emerald-100/50">
          {sourceName ? <span>{sourceName}</span> : null}
          {article.publishedAt ? (
            <>
              {sourceName ? <span aria-hidden>·</span> : null}
              <time dateTime={article.publishedAt}>
                {formatNewsDate(article.publishedAt)}
              </time>
            </>
          ) : null}
        </div>
        <h3
          className={
            compact
              ? "line-clamp-2 text-sm font-semibold text-white group-hover:text-emerald-300"
              : "text-base font-semibold text-white group-hover:text-emerald-300 sm:text-lg"
          }
        >
          {article.title}
        </h3>
        {!compact && article.description ? (
          <p className="mt-2 line-clamp-3 text-sm text-emerald-100/70">
            {article.description}
          </p>
        ) : null}
        <p className="mt-2 text-xs font-medium text-emerald-400 group-hover:text-emerald-300">
          Read article →
        </p>
      </div>
    </a>
  );
}
