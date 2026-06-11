"""Entry point for VS Code / uvicorn: `uvicorn main:app --reload` also works."""

from app import app

__all__ = ["app"]


def main():
    import uvicorn

    uvicorn.run("app:app", host="127.0.0.1", port=8000, reload=True)


if __name__ == "__main__":
    main()
