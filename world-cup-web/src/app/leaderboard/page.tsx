import { LeaderboardTable } from "@/components/leaderboard/leaderboard-table";

export const metadata = { title: "Leaderboard" };

export default function LeaderboardPage() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-3xl font-bold text-white">Leaderboard</h1>
      <p className="mt-2 text-emerald-100/70">
        Top predictors ranked by correct bets throughout the tournament.
      </p>

      <div className="mt-8">
        <LeaderboardTable />
      </div>
    </div>
  );
}
