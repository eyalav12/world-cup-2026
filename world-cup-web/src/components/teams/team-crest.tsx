import Image from "next/image";
import { getTeamImageUrl } from "@/lib/teams";
import { cn } from "@/lib/utils";

export function TeamCrest({
  teamName,
  size = 40,
  className,
}: {
  teamName: string | null | undefined;
  size?: number;
  className?: string;
}) {
  const name = teamName?.trim() || "TBD";
  const src = getTeamImageUrl(name);

  if (!src) {
    const initials = name
      .split(" ")
      .map((w) => w[0])
      .join("")
      .slice(0, 3)
      .toUpperCase();

    return (
      <div
        className={cn(
          "flex shrink-0 items-center justify-center rounded-full bg-emerald-600/30 text-xs font-bold text-emerald-100",
          className,
        )}
        style={{ width: size, height: size }}
        title={name}
      >
        {initials}
      </div>
    );
  }

  return (
    <Image
      src={src}
      alt={`${name} crest`}
      width={size}
      height={size}
      className={cn("shrink-0 rounded-full object-cover", className)}
      unoptimized
    />
  );
}
